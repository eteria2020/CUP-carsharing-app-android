package it.sharengo.development.ui.base.map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import it.handroix.core.base.HdxBaseActivity;
import it.handroix.location.HdxActivityLocationHelper;
import it.handroix.location.HdxLocationUpdateListener;
import it.handroix.map.HdxFragmentMapHelper;
import it.sharengo.development.R;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;

public abstract class BaseMapFragment<T extends BaseMapPresenter> extends BaseMvpFragment<T>
        implements MvpMapView, HdxLocationUpdateListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMyLocationChangeListener {

    public static final Integer DISABLEDLOCATION_ZOOM = 5;
    public static final Integer DEFAULT_ZOOM = 13;
    private static final double DEFAULT_LAT = 41.931543;
    private static final double DEFAULT_LON = 12.503420;

    protected HdxActivityLocationHelper mHdxActivityLocationHelper;
    protected HdxFragmentMapHelper mMapHelper;
    protected GoogleMap mMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHdxActivityLocationHelper = HdxActivityLocationHelper.newInstance((HdxBaseActivity) getActivity());
        mHdxActivityLocationHelper.setupLocationOnDemandMode(this);

    }

    //*********************************
    //
    //          LOCATION CALLBACKs
    //
    //*********************************

    @Override
    public void onNewLocation(Location location) {
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(this);
        }
    }

    @Override
    public void onLocationUnavailable() {
        // STUB
    }

    @Override
    public void onRequestRationalFineLocationPermission() {
        new AlertDialog.Builder(getActivity())
                .setMessage(getActivity().getString(R.string.msg_rational_location_permission))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHdxActivityLocationHelper.requestRuntimeFineLocationPermissionAfterRationale();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    @Override
    public void onFineLocationDenied() {
        // STUB
        Log.w("LOCATION","onFineLocationDenied");
    }

    //*********************************
    //
    //          MAP CALLBACKs
    //
    //*********************************

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), "android.permission.ACCESS_FINE_LOCATION");
        if (hasFineLocationPermission == 0) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(this);

            if (mHdxActivityLocationHelper.getLastLocation() != null) {
                moveMapCameraTo(mHdxActivityLocationHelper.getLastLocation().getLatitude(), mHdxActivityLocationHelper.getLastLocation().getLongitude());
            }
        }

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    //*********************************
    //
    //          VIEW METHODS
    //
    //*********************************

    @Override
    public void moveMapCameraTo(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mMap.moveCamera(cu);
    }

    @Override
    public void animateMapCamera(Double latitude, Double longitude) {
        animateMapCamera(latitude, longitude, DEFAULT_ZOOM);
    }

    @Override
    public void animateMapCamera(Double latitude, Double longitude, float zoom) {

        if (latitude == null || longitude == null) {
            return;
        }

        LatLng latLng = new LatLng(latitude, longitude);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.animateCamera(cu);
    }

    @Override
    public void moveMapCameraToDefaultLocation() {
        LatLng latLng = new LatLng(DEFAULT_LAT, DEFAULT_LON);
        CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latLng, DISABLEDLOCATION_ZOOM);
        mMap.moveCamera(cu);
    }

    @Override
    public void showEmptyMap() {
        mMap.clear();
    }


    @Override
    public void onMyLocationChange(Location location) {
        Log.w("LOCATION","onMyLocationChange");
    }
}