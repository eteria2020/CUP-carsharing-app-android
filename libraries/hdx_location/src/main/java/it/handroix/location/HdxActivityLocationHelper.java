package it.handroix.location;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import it.handroix.core.base.HdxBaseActivity;
import it.handroix.core.proxy.HdxActivityProxy;

public class HdxActivityLocationHelper implements HdxActivityProxy,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final String TAG = "HdxLocationHelper";

    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    protected GoogleApiClient mGoogleApiClient;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

    //Location Update Configuration for OnDemand Mode
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 333;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    private HdxBaseActivity mActivity;
    private Location mLastKnownLocation;
    private LocationRequest mLocationRequest;
    private boolean firstLaunch = true;
    private boolean onDemandMode = true;

    private HdxLocationUpdateListener mUpdateListener;
    private ResultCallback<LocationSettingsResult> mLocationSettingsResultCallback;


    public static HdxActivityLocationHelper newInstance(HdxBaseActivity activity) {
        HdxActivityLocationHelper mInstance = new HdxActivityLocationHelper(activity);
        mInstance.mActivity.addProxy(mInstance);
        return mInstance;
    }

    private HdxActivityLocationHelper(HdxBaseActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Setup the Location manager in onDemand mode,
     * this means that when the first location is available callback will be fired but after that
     * you can request the Location by calling getLastLocation()
     *
     * @param updateListener callback listener for firs location
     */
    public void setupLocationOnDemandMode(HdxLocationUpdateListener updateListener) {
        onDemandMode = true;
        mUpdateListener = updateListener;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (requestRuntimeFineLocationPermission()) {
            if (servicesAvalable()) {
                buildGoogleApiClient();
            }
        }
    }

    public void setupLocationOnDemandMode(HdxLocationUpdateListener updateListener, ResultCallback<LocationSettingsResult> locationSettingsResultCallback) {
        mLocationSettingsResultCallback = locationSettingsResultCallback;
        setupLocationOnDemandMode(updateListener);
    }


    /**
     * Setup the Location manager in Listeners mode,
     * this means you will continuosly receive location update at configured time interval
     *
     * @param updateListener  callback listener
     * @param accuracy        accuracy constant see LocationRequest es(LocationRequest.PRIORITY_HIGH_ACCURACY)
     * @param interval        time interval in millis between update
     * @param fastestInterval shortest time interval in millis between update if position changed
     */
    public void setupLocationListenersMode(HdxLocationUpdateListener updateListener, int accuracy, long interval, long fastestInterval) {
        onDemandMode = false;
        mUpdateListener = updateListener;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        mLocationRequest.setPriority(accuracy);

        if (requestRuntimeFineLocationPermission()) {
            if (servicesAvalable()) {
                buildGoogleApiClient();
            }
        }
    }

    public void setupLocationListenersMode(HdxLocationUpdateListener updateListener, int accuracy, long interval, long fastestInterval, ResultCallback<LocationSettingsResult> locationSettingsResultCallback) {
        mLocationSettingsResultCallback = locationSettingsResultCallback;
        setupLocationListenersMode(updateListener, accuracy, interval, fastestInterval);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null && mLocationRequest != null && mLocationSettingsResultCallback != null) {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);
            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(mLocationSettingsResultCallback);
        }
    }


    @Override
    public void onStart() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    //**********************************************************
    // PERMISSION MANAGEMENT
    //*********************************************************

    public void requestRuntimeFineLocationPermissionAfterRationale() {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
    }

    private boolean requestRuntimeFineLocationPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                mUpdateListener.onRequestRationalFineLocationPermission();
                return false;
            } else {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (servicesAvalable()) {
                        buildGoogleApiClient();
                        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
                            mGoogleApiClient.connect();
                    }
                } else {
                    mUpdateListener.onFineLocationDenied();
                }
            }
        }
    }

    //**********************************************************
    // LOCATION MANAGEMENT
    //*********************************************************

    /**
     * Get the last available Location
     *
     * @return
     */
    public Location getLastLocation() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Location tmpLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (tmpLocation != null) {
                mLastKnownLocation = tmpLocation;
            }
        }
        return mLastKnownLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastKnownLocation = location;
        if (mLastKnownLocation != null) {
            if (onDemandMode) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                firstLaunch = false;
            }
            mUpdateListener.onNewLocation(mLastKnownLocation);
        }
    }

    //**********************************************************
    // API CLIENT MANAGEMENT
    //*********************************************************

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "LocationClient Connected");

        //QUESTO CONTROLLO SERVE PER OVVIARE A PROBLEMI DOVUTI A PIU'' CLIENT INSTANZIATI IN PIU' FRAGMENT
        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (onDemandMode) {
            if (mLastKnownLocation == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                mUpdateListener.onLocationUnavailable();
            } else {
                if (firstLaunch) {
                    if (mUpdateListener != null) {
                        mUpdateListener.onNewLocation(mLastKnownLocation);
                    }
                    firstLaunch = false;
                }
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            if (mLastKnownLocation != null) {
                mUpdateListener.onNewLocation(mLastKnownLocation);
            } else {
                mUpdateListener.onLocationUnavailable();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "LocationClient onConnectionFailed error: " + connectionResult);

        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(mActivity, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                Log.e(TAG, "LocationClient onConnectionFailed: ", e);
            }
        } else {
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(mActivity,
                    connectionResult.getErrorCode(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(mActivity.getFragmentManager(), "Location Updates");
            }
        }
    }

    /**
     * Google Play services error dialog
     */
    public static class ErrorDialogFragment extends DialogFragment {
        private Dialog mDialog;

        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    /**
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        buildGoogleApiClient();
                        mGoogleApiClient.connect();
                        break;
                }
        }
    }

    /**
     * Check if google Play services is available and if not launch error dialog
     *
     * @return
     */
    private boolean servicesAvalable() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mActivity);
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(TAG, "Google Play services is available.");
            return true;
        } else {
            Log.e(TAG, "Google Play services NOT available.");
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(mActivity, resultCode, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(mActivity.getFragmentManager(), "Location Updates");
            }
            return false;
        }
    }

    //UNUSED PROXY METHOD
    @Override
    public void onPause() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
