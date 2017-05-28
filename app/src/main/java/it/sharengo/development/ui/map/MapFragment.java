package it.sharengo.development.ui.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.DirectedLocationOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.sharengo.development.R;
import it.sharengo.development.data.models.Car;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;



public class MapFragment extends BaseMvpFragment<MapPresenter> implements MapMvpView, LocationListener {

    private static final String TAG = MapFragment.class.getSimpleName();

    /*private GeoPoint currentLocation;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private IMapController mapController;
    private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
    private OverlayItem pinUser;
    private ArrayList<OverlayItem> items;*/

    private ItemizedOverlayWithFocus<OverlayItem> mOverlay;
    private View view;

    private boolean hasInit = false;
    private DirectedLocationOverlay overlay;
    private GeoPoint userLocation;
    private GeoPoint defaultLocation = new GeoPoint(41.931543, 12.503420);
    private ArrayList<OverlayItem> items;
    private RotateAnimation anim;
    private String carnext_id;
    private Car carSelected;

    private float currentRotation = 0f;

    @BindView(R.id.mapView)
    MapView mMapView;

    @BindView(R.id.centerMapButton)
    ImageView centerMapButton;

    @BindView(R.id.refreshMapButton)
    ImageView refreshMapButton;

    @BindView(R.id.mapOverlayView)
    View mapOverlayView;

    @BindView(R.id.popupCarView)
    View popupCarView;

    @BindView(R.id.carImageView)
    ImageView carImageView;

    @BindView(R.id.plateTextView)
    TextView plateTextView;

    @BindView(R.id.autonomyTextView)
    TextView autonomyTextView;

    @BindView(R.id.addressTextView)
    TextView addressTextView;

    @BindView(R.id.distanceView)
    ViewGroup distanceView;

    @BindView(R.id.distanceTextView)
    TextView distanceTextView;

    @BindView(R.id.timeView)
    ViewGroup timeView;

    @BindView(R.id.timeTextView)
    TextView timeTextView;

    @BindView(R.id.closestcarView)
    ViewGroup closestcarView;

    @BindView(R.id.searchEditText)
    EditText searchEditText;

    @BindView(R.id.orientationMapButton)
    ImageView orientationMapButton;


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

        //Rotate animation - refresh button
        anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);




        addOverlays();

        if (mMapView!=null) {
            final Context context = this.getActivity();
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();


            mMapView.setBuiltInZoomControls(false);
            mMapView.setMultiTouchControls(true);
            mMapView.setTilesScaledToDpi(true);
            mMapView.getController().setZoom(14);
            mMapView.setMapListener(new MapListener() {
                @Override
                public boolean onScroll(ScrollEvent event) {
                    if(hasInit) {
                        refreshMapButton.startAnimation(anim);
                        refreshCars();

                        setRotationButton(mMapView.getMapOrientation());
                    }
                    return false;
                }

                @Override
                public boolean onZoom(ZoomEvent event) {
                    if(hasInit) {
                        refreshMapButton.startAnimation(anim);
                        refreshCars();
                    }
                    return false;
                }
            });

            RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(context, mMapView);
            mRotationGestureOverlay.setEnabled(true);
            mMapView.getOverlays().add(mRotationGestureOverlay);
        }

        return view;
    }

    private void setRotationButton(float rotation){
        RotateAnimation animCompass = new RotateAnimation(currentRotation, rotation,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        currentRotation = rotation % 360;

        animCompass.setInterpolator(new LinearInterpolator());
        animCompass.setDuration(1000);
        animCompass.setFillEnabled(true);

        animCompass.setFillAfter(true);
        orientationMapButton.setAnimation(animCompass);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Preparo il popup dell'auto per l'animazione di entrata
        popupCarView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int width = popupCarView.getWidth();
                int height = popupCarView.getHeight();
                if (width > 0 && height > 0) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        popupCarView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        popupCarView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    popupCarView.setTranslationY(popupCarView.getHeight());
                }
            }
        });


        refreshMapButton.startAnimation(anim);

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
        mMapView.getOverlays().add(overlay);


    }

    @Override
    public void onLocationChanged(Location location) {

        userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

        userLocation = new GeoPoint(45.538927, 9.168744); //TODO: remove

        //First time
        if (!hasInit){

            BitmapDrawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = (BitmapDrawable) getActivity().getDrawable(R.drawable.ic_user);
            }else{
                drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_user);
            }

            overlay.setDirectionArrow(drawable.getBitmap());

            centerMap();
            refreshCars();
        }

        hasInit = true;
        overlay.setBearing(location.getBearing());
        overlay.setAccuracy((int)location.getAccuracy());
        overlay.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
        mMapView.invalidate();


        enabledCenterMap(true);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        userLocation = null;

        if (!hasInit){
            mMapView.getController().setCenter(defaultLocation);
            mMapView.getController().setZoom(5);
            loadsCars();
        }
        enabledCenterMap(false);
    }

    private void loadsCars(){
        mPresenter.loadCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), 100);
    }

    private void refreshCars(){
        mPresenter.refreshCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), 100);
    }

    private void centerMap(){
        if(userLocation != null) {
            mMapView.getController().setCenter(userLocation);
            mMapView.getController().setZoom(14);
        }else{
            new AlertDialog.Builder(getActivity())
                    .setMessage(getActivity().getString(R.string.maps_permissionlocation_alert))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openSettings();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create()
                    .show();
        }
    }

    private void orientationMap(){
        mMapView.setMapOrientation(0);
        orientationMapButton.setRotation(0.0f);
        setRotationButton(0.0f);
    }

    private void enabledCenterMap(boolean enable){
        if(enable){
            centerMapButton.setAlpha(1.0f);
        }else{
            centerMapButton.setAlpha(.4f);
        }
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


    private BitmapDrawable getIconMarker(int icon){
        BitmapDrawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = (BitmapDrawable) getActivity().getDrawable(icon);
        }else{
            drawable = (BitmapDrawable) getResources().getDrawable(icon);
        }

        return drawable;
    }

    private String findNextCar(List<Car> carsList){

        String car_id = "";
        float distance = 100000.0f;


        if(userLocation != null) {

            for (Car car : carsList) {

                float dist = getDistance(car);

                if(dist < distance) {
                    distance = dist;
                    car_id = car.id;
                }
            }
        }

        return car_id;
    }

    private IGeoPoint getMapCenter(){
        return  mMapView.getMapCenter();
    }

    private void showPopupCar(Car car){

        carSelected = car;

        // Animazione
        popupCarView.setVisibility(View.VISIBLE);
        popupCarView.animate().translationY(0);

        //Popolo i dati dell'interfaccia

        //Targa
        plateTextView.setText(car.id);

        //Autonomia
        autonomyTextView.setText(String.format(getString(R.string.maps_autonomy_label), car.autonomy));

        //Indirizzo
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(car.latitude, car.longitude, 1);
            if(!addresses.isEmpty())
                addressTextView.setText(addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Distanza
        if(userLocation != null){
            distanceTextView.setText(String.format(getString(R.string.maps_distance_label), Math.round(getDistance(car))));
        }else{
            distanceView.setVisibility(View.GONE);
        }

        //Time
        if(userLocation != null){
            timeTextView.setText(String.format(getString(R.string.maps_time_label), Math.round(getDistance(car)/100)));
        }else{
            timeView.setVisibility(View.GONE);
        }

        //Tipologia popup
        if(car.id.equals(carnext_id)){
            closestcarView.setVisibility(View.VISIBLE);
        }else{
            closestcarView.setVisibility(View.GONE);
        }
    }

    private float getDistance(Car car){
        Location locationA = new Location("point A");

        locationA.setLatitude(userLocation.getLatitude());
        locationA.setLongitude(userLocation.getLongitude());

        Location locationB = new Location("point B");

        locationB.setLatitude(car.latitude);
        locationB.setLongitude(car.longitude);

        return locationA.distanceTo(locationB);
    }

    private void openSettings(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.searchEditText)
    public void onSearchClick(){
        onClosePopup();
    }

    @OnClick(R.id.microphoneImageView)
    public void onSearchMicrophone(){
        onClosePopup();
    }

    @OnClick(R.id.centerMapButton)
    public void onCenterMap() {
        centerMap();
    }

    @OnClick(R.id.orientationMapButton)
    public void onOrientationMap() {
        orientationMap();
    }

    @OnClick(R.id.refreshMapButton)
    public void onRefreshMap() {
        refreshMapButton.startAnimation(anim);
        mPresenter.refreshCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), 100);
    }

    @OnClick(R.id.closePopupButton)
    public void onClosePopup() {
        popupCarView.animate().translationY(popupCarView.getHeight());
    }

    @OnClick(R.id.openDoorButton)
    public void onOpenDoor(){
        if(carSelected != null){
            if(userLocation != null){
                //Calcolo la distanza
                if(getDistance(carSelected) <= 50){
                    //Procediamo con le schermate successive
                }else{
                    new AlertDialog.Builder(getActivity())
                            .setMessage(getActivity().getString(R.string.maps_opendoordistance_alert))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                }
            }else{
                //Informo l'utente che la localizzazione non è attiva
                new AlertDialog.Builder(getActivity())
                        .setMessage(getActivity().getString(R.string.maps_permissionopendoor_alert))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openSettings();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
            }
        }
    }

    @OnClick(R.id.bookingCarButton)
    public void onBookingCar(){

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void showCars(final List<Car> carsList) {

        //Trovo la macchina più vicina a me
        carnext_id = findNextCar(carsList);

        Log.w("carnext_id",": "+carnext_id);

        //Marker array
        items = new ArrayList<OverlayItem>();

        for(Car car : carsList){
            //Verifico che la macchina sia in status = operative
            if(car.status.equals("operative")) {
                int icon_marker = R.drawable.ic_auto;

                //Verifico se la vettura è la più vicina
                if(car.id.equals(carnext_id)){
                    icon_marker = R.drawable.ic_auto_vicina;
                }

                //Creo il marker
                OverlayItem overlayItem = new OverlayItem(car.manufactures, "Descrizione", new GeoPoint(car.latitude, car.longitude));
                overlayItem.setMarker(getIconMarker(icon_marker));
                items.add(overlayItem);
            }
        }

        if(mOverlay != null)
            mOverlay.removeAllItems();

        mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(
                getActivity(), items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        showPopupCar(carsList.get(index));
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });
        //mOverlay.setFocusItemsOnTap(true);

        mMapView.getOverlays().add(mOverlay);
        mMapView.invalidate();

        Log.w("FINISH","OK");

        //Stop sull'animazione del pulsante di refresh
        anim.cancel();

    }

}
