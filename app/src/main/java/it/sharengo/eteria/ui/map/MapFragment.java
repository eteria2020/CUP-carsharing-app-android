package it.sharengo.eteria.ui.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.fragments.BaseMvpFragment;


public class MapFragment extends BaseMvpFragment<MapPresenter> implements MapMvpView, LocationListener {

    private static final String TAG = MapFragment.class.getSimpleName();

    /*private GeoPoint currentLocation;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private IMapController mapController;
    private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
    private OverlayItem pinUser;
    private ArrayList<OverlayItem> items;

    private ItemizedOverlayWithFocus<OverlayItem> mOverlay;*/
    private View view;

    private boolean hasFix = false;
    private DirectedLocationOverlay overlay;

    @BindView(R.id.mapView)
    MapView mMapView;


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        //setUpMap();

        if (Build.VERSION.SDK_INT >= 12) {
            mMapView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
                /**
                 * mouse wheel zooming ftw
                 * http://stackoverflow.com/questions/11024809/how-can-my-view-respond-to-a-mousewheel
                 * @param v
                 * @param event
                 * @return
                 */
                @Override
                public boolean onGenericMotion(View v, MotionEvent event) {
                    if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_SCROLL:
                                if (event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f)
                                    mMapView.getController().zoomOut();
                                else {
                                    mMapView.getController().zoomIn();
                                }
                                return true;
                        }
                    }
                    return false;
                }
            });
        }

        addOverlays();

        if (mMapView!=null) {
            final Context context = this.getActivity();
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();

            CopyrightOverlay copyrightOverlay = new CopyrightOverlay(getActivity());

            //i hate this very much, but it seems as if certain versions of android and/or
            //device types handle screen offsets differently
            if (Build.VERSION.SDK_INT <= 10)
                copyrightOverlay.setOffset(0,(int)(55*dm.density));

            mMapView.getOverlays().add(copyrightOverlay);
            mMapView.setBuiltInZoomControls(true);
            mMapView.setMultiTouchControls(true);
            mMapView.setTilesScaledToDpi(true);
        }

        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG, "onDetach");
        if (mMapView!=null)
            mMapView.onDetach();
        mMapView=null;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            //on API15 AVDs,network provider fails. no idea why
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) this);
                return;
            }

        }
        catch (Exception ex){}
    }

    @Override
    public void onPause(){
        super.onPause();
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);

    }

    private void addOverlays() {
        overlay = new DirectedLocationOverlay(getActivity());
        overlay.setShowAccuracy(true);
        Toast.makeText(getActivity(), "Requires location services turned on", Toast.LENGTH_LONG).show();
        mMapView.getOverlays().add(overlay);


    }

    @Override
    public void onLocationChanged(Location location) {
        //after the first fix, schedule the task to change the icon
        if (!hasFix){
            Toast.makeText(getActivity(), "Location fixed, scheduling icon change", Toast.LENGTH_LONG).show();
            TimerTask changeIcon = new TimerTask() {
                @Override
                public void run() {
                    Activity act = getActivity();
                    if (act!=null)
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.pin_car);
                                    overlay.setDirectionArrow(drawable.getBitmap());
                                }catch (Throwable t){
                                    //insultates against crashing when the user rapidly switches fragments/activities
                                }
                            }
                        });

                }
            };
            Timer timer = new Timer();
            timer.schedule(changeIcon, 5000);

        }
        hasFix=true;
        overlay.setBearing(location.getBearing());
        overlay.setAccuracy((int)location.getAccuracy());
        overlay.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
        mMapView.invalidate();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    /*private void setUpMap() {

        //PIN
        items = new ArrayList<OverlayItem>();



        locationListener = new MyLocationListener();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if( location != null ) {
                currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
            }else{
                currentLocation = new GeoPoint(45.463932, 9.186487);
            }
        }


        mMapView.destroyDrawingCache();
        mMapView.refreshDrawableState();
        mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);


        mapController = mMapView.getController();
        mapController.setZoom(14);





        //Aggiungo il pin utente
        pinUser = new OverlayItem("Title", "Description", currentLocation);
        items.add(pinUser);



        mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(
                getActivity(), items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });
        mOverlay.setFocusItemsOnTap(true);
        mOverlay.removeItem(pinUser);

        mMapView.getOverlays().add(mOverlay);


        displayMyCurrentLocationOverlay();
        if( currentLocation != null) {

            mapController.setCenter(currentLocation);
        }

    }

    private void displayMyCurrentLocationOverlay() {

        Log.w("currentLocation",": "+currentLocation);


        mOverlay.removeItem(pinUser);

        pinUser = new OverlayItem("Title", "Description", currentLocation);

        mOverlay.addItem(pinUser);

        mMapView.invalidate();

    }

    ublic class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {
            currentLocation = new GeoPoint(location);
            displayMyCurrentLocationOverlay();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }*/




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


}
