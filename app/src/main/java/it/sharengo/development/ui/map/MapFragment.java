package it.sharengo.development.ui.map;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import org.apache.commons.lang3.StringUtils;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import it.sharengo.development.R;
import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;


public class MapFragment extends BaseMvpFragment<MapPresenter> implements MapMvpView, LocationListener {

    private static final String TAG = MapFragment.class.getSimpleName();

    /*private GeoPoint currentLocation;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private IMapController mapController;
    private ItemizedOverlay<OverlayItem> mMyLocationOverlay;
    private OverlayItem pinUser;
    private ArrayList<OverlayItem> items;*/

    private final int SPEECH_RECOGNITION_CODE = 1;
    private final int ZOOM_A = 15;
    private final int ZOOM_B = 5;

    private ItemizedOverlayWithFocus<OverlayItem> mOverlay;
    private ItemizedOverlayWithFocus<OverlayItem> mOverlayUser;
    private View view;

    private boolean hasInit = false;
    private DirectedLocationOverlay overlay;
    private GeoPoint userLocation;
    private GeoPoint defaultLocation = new GeoPoint(41.931543, 12.503420);
    private ArrayList<OverlayItem> items;
    private RotateAnimation anim;
    private String carnext_id;
    private Car carSelected;
    private OverlayItem pinUser;

    private float currentRotation = 0f;

    private MapSearchListAdapter mAdapter;
    private MapSearchListAdapter.OnItemActionListener mActionListener = new MapSearchListAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(SearchItem searchItem) {
            hideSoftKeyboard();
            mMapView.getController().setCenter(new GeoPoint(searchItem.latitude, searchItem.longitude));
            mMapView.getController().zoomTo(ZOOM_A);
            mMapView.getController().animateTo(new GeoPoint(searchItem.latitude, searchItem.longitude));
        }
    };

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

    @BindView(R.id.searchMapResultView)
    ViewGroup searchMapResultView;

    @BindView(R.id.orientationMapButton)
    ImageView orientationMapButton;

    @BindView(R.id.microphoneImageView)
    ImageView microphoneImageView;

    @BindView(R.id.searchRecyclerView)
    RecyclerView searchRecyclerView;


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);

        mAdapter = new MapSearchListAdapter(mActionListener);
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


        //addOverlays();

        if (mMapView!=null) {

            final Context context = this.getActivity();
            final DisplayMetrics dm = context.getResources().getDisplayMetrics();

            mMapView.setBuiltInZoomControls(false);
            mMapView.setMultiTouchControls(true);
            mMapView.setTilesScaledToDpi(true);
            mMapView.getController().setZoom(ZOOM_A);
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

        //Ricerca
        final LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        searchRecyclerView.setLayoutManager(lm);
        searchRecyclerView.setAdapter(mAdapter);
        //searchRecyclerView.addItemDecoration(new DividerItemDecoration(searchRecyclerView.getContext(), lm.getOrientation()));


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

        //Verifico se il dispositivo non è abilitato alla dettatura (microfono)
        if(!SpeechRecognizer.isRecognitionAvailable(getActivity())){
            microphoneImageView.setVisibility(View.GONE);
        }

        view.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

        //Prelevo le targhe
        mPresenter.viewCreated();
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG, "onDetach");
        if (mMapView!=null)
            mMapView.onDetach();
        mMapView=null;

        if(view != null)
            view.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);

        mPresenter.viewDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
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

    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            setKeyboardListener();
        }
    };

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

            /*BitmapDrawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = (BitmapDrawable) getActivity().getDrawable(R.drawable.ic_user);
            }else{
                drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_user);
            }

            overlay.setDirectionArrow(drawable.getBitmap());*/


            pinUser = new OverlayItem("Title", "Description", userLocation);
            ArrayList<OverlayItem> userOverleyList = new ArrayList<OverlayItem>();
            userOverleyList.add(pinUser);
            mOverlayUser = new ItemizedOverlayWithFocus<OverlayItem>(
                    getActivity(), userOverleyList,
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

            mMapView.getOverlays().add(mOverlayUser);

            displayMyCurrentLocationOverlay();

            centerMap();
            refreshCars();
        }

        hasInit = true;
        //overlay.setBearing(location.getBearing());
        //overlay.setAccuracy((int)location.getAccuracy());
        //overlay.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
        //mMapView.invalidate();



        pinUser = new OverlayItem("Title", "Description", userLocation);

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
            mMapView.getController().setZoom(ZOOM_B);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshCars();
                }
            }, 100);

        }
        enabledCenterMap(false);

    }

    private void loadsCars(){
        mPresenter.loadCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), 1000000000);
    }

    private void refreshCars(){

        try {
            mPresenter.refreshCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), 1000000000);
        }catch (NullPointerException e){}

    }

    private void centerMap(){

        if(userLocation != null) {
            mMapView.getController().setCenter(userLocation);
            mMapView.getController().setZoom(14);
        }else{

            final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                    getString(R.string.maps_permissionlocation_alert),
                    getString(R.string.ok),
                    getString(R.string.cancel));
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                    openSettings();
                }
            });
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


    private void displayMyCurrentLocationOverlay() {
        mOverlayUser.removeAllItems();

        pinUser = new OverlayItem("Title", "Description", userLocation);
        pinUser.setMarker(getIconMarker(R.drawable.ic_user));

        mOverlayUser.addItem(pinUser);

        mMapView.invalidate();

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
        float distance = 10000000000000000000000.0f;


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
        autonomyTextView.setText(Html.fromHtml(String.format(getString(R.string.maps_autonomy_label), car.autonomy)));

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
            String distanceString = "";
            float distance = getDistance(car);
            if(distance < 1000){
                distance = Math.round(distance);
                distanceString = String.format(getString(R.string.maps_distance_label), distance);
            }
            else{
                distanceString = String.format(getString(R.string.maps_distancekm_label), distance/1000);
            }

            distanceTextView.setText(distanceString);
        }else{
            distanceView.setVisibility(View.GONE);
        }

        //Time
        if(userLocation != null){
            int timeF = Math.round(getDistance(car)/100);
            String timeFString = "";

            if(timeF < 60){
                timeFString = String.format(getString(R.string.maps_time_label), timeF);
            }else if(timeF == 60){
                timeFString = getString(R.string.maps_timeh60_label);
            }else if(timeF > 60){
                int hh = timeF / 60;
                int mm = timeF % 60;

                if(mm == 0){
                    timeFString = String.format(getString(R.string.maps_timeh_label), hh);
                }else {
                    if(hh == 1){
                        timeFString = String.format(getString(R.string.maps_timehsm_label), hh, mm);
                    }else {
                        timeFString = String.format(getString(R.string.maps_timehm_label), hh, mm);
                    }
                }
            }

            timeTextView.setText(timeFString);
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

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.maps_searchmicrophone_message));
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Snackbar.make(view, getString(R.string.error_generic_msg), Snackbar.LENGTH_LONG).show();
        }
    }


    /**
     * Callback for speech recognition activity
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    //searchEditText.setText(text);
                }
                break;
            }

        }
    }

    private void setSearchResult(){
        //int chardigit = (int) searchEditText.getTextSize();

        searchMapResultView.setVisibility(View.VISIBLE);
    }

    private void setKeyboardListener(){
        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);
        if (view.getRootView().getHeight() - (r.bottom - r.top) > 500) {
            //Show
            setSearchResult();
        } else {
            //Hidden
            searchMapResultView.setVisibility(View.GONE);
        }
    }

    private void initMapSearch(){
        String searchMapText = searchEditText.getText().toString();
        if (searchMapText.length() > 2) {
            Log.w("SEARCH","YES");

            //Verifico se è una targa: (con pattern 2 lettere + 1 numero Es: AB1 ) è una targa e quindi cerchiamo tra le targhe, altrimenti cerchiamo l'indirizzo
            if(!StringUtils.isNumeric(searchMapText.substring(0))
                && !StringUtils.isNumeric(searchMapText.substring(1))
                  && StringUtils.isNumeric(searchMapText.substring(2))){
                Log.w("SEARCH","PLATE");
                mPresenter.findPlates(searchMapText);
            }else{
                Log.w("SEARCH","ADDRESS");
            }

        }else{
            Log.w("SEARCH","NO");
            mAdapter.setData(null);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @OnFocusChange(R.id.searchEditText)
    public void OnFocusSearchChange(){
        //setSearchResult(); TODO
    }

    @OnClick(R.id.searchEditText)
    public void onSearchClick(){
        //setSearchResult(); TODO
    }

    @OnTextChanged(R.id.searchEditText)
    public void searchEditText(){
        initMapSearch();
    }

    @OnClick(R.id.microphoneImageView)
    public void onSearchMicrophone(){
        onClosePopup();
        startSpeechToText();
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
        mPresenter.refreshCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), 1000000000);
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
                    CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                            getString(R.string.maps_opendoordistance_alert),
                            getString(R.string.ok),
                            null);
                    cdd.show();
                }
            }else{
                //Informo l'utente che la localizzazione non è attiva
                final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                        getString(R.string.maps_permissionopendoor_alert),
                        getString(R.string.ok),
                        getString(R.string.cancel));
                cdd.show();
                cdd.yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cdd.dismissAlert();
                        openSettings();
                    }
                });

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
    boolean alphaDirection = true;
    int currentOverlayAlpha = 0;
    OverlayItem ccOverlay;
    int currentDrawable = 0;
    Drawable[] intArray = new Drawable[7];
    Timer timer;
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

                if(car.id.equals(carnext_id)){
                    ccOverlay = overlayItem;
                }

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



        intArray[0] = getResources().getDrawable(R.drawable.tmp0);
        intArray[1] = getResources().getDrawable(R.drawable.tmp1);
        intArray[2] = getResources().getDrawable(R.drawable.tmp2);
        intArray[3] = getResources().getDrawable(R.drawable.tmp3);
        intArray[4] = getResources().getDrawable(R.drawable.tmp4);
        intArray[5] = getResources().getDrawable(R.drawable.tmp5);
        intArray[6] = getResources().getDrawable(R.drawable.tmp6);


        /*
        if(timer != null) timer.cancel();

        timer = new Timer();


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentOverlayAlpha > 255) {
                            currentOverlayAlpha = 255;
                            alphaDirection = !alphaDirection;
                        } else if (currentOverlayAlpha < 0) {
                            currentOverlayAlpha = 0;
                            alphaDirection = !alphaDirection;
                        }

                        //ccOverlay.getDrawable().setAlpha(currentOverlayAlpha);
                        ccOverlay.setMarker(intArray[currentDrawable]);
                        mMapView.invalidate();


                        if (alphaDirection) {
                            currentOverlayAlpha += 20;
                        } else {
                            currentOverlayAlpha -= 20;
                        }

                        if(currentDrawable < intArray.length-1) currentDrawable++;
                        else currentDrawable = 0;

                    }
                });
            }
        }, 150, 150);*/


        //Stop sull'animazione del pulsante di refresh
        anim.cancel();

    }

    @Override
    public void noCarsFound() {
        //Stop sull'animazione del pulsante di refresh
        anim.cancel();
    }

    @Override
    public void showSearchResult(List<SearchItem> searchItemList) {
        mAdapter.setData(searchItemList);
    }

}
