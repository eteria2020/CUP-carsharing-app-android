package it.sharengo.development.ui.map;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
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
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
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
import it.sharengo.development.data.models.Reservation;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;

import static android.content.Context.MODE_PRIVATE;
import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static it.sharengo.development.R.id.deleteBookingButton;


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
    private final int ZOOM_C = 18;

    private ItemizedOverlayWithFocus<OverlayItem> mOverlay;
    private ItemizedOverlayWithFocus<OverlayItem> mOverlayUser;
    private View view;

    private boolean hasInit = false;
    private DirectedLocationOverlay overlay;
    private GeoPoint userLocation;
    private GeoPoint defaultLocation = new GeoPoint(41.931543, 12.503420);
    private ArrayList<OverlayItem> items;
    private RadiusMarkerClusterer poiMarkers;
    private FolderOverlay poiCityMarkers;
    private RotateAnimation anim;
    private String carnext_id;
    private Car carSelected;
    private OverlayItem pinUser;
    private boolean searchViewOpen = false;
    private Marker carnextMarker, carbookingMarker;
    private int currentDrawable = 0; //frame dell'animazione della macchiana più vicina
    private int[] drawableAnimGreenArray = new int[]{R.drawable.autopulse0001, R.drawable.autopulse0002, R.drawable.autopulse0003, R.drawable.autopulse0004, R.drawable.autopulse0005, R.drawable.autopulse0006, R.drawable.autopulse0007, R.drawable.autopulse0008, R.drawable.autopulse0009, R.drawable.autopulse0010, R.drawable.autopulse0011, R.drawable.autopulse0012, R.drawable.autopulse0013, R.drawable.autopulse0014, R.drawable.autopulse0015, R.drawable.autopulse0016, R.drawable.autopulse0017, R.drawable.autopulse0018, R.drawable.autopulse0019, R.drawable.autopulse0020 };
    private int[] drawableAnimYellowArray = new int[]{R.drawable.autopulse0001, R.drawable.autopulse0002, R.drawable.autopulse0003, R.drawable.autopulse0004, R.drawable.autopulse0005, R.drawable.autopulse0006, R.drawable.autopulseyellow0007, R.drawable.autopulseyellow0008, R.drawable.autopulseyellow0009, R.drawable.autopulseyellow0010, R.drawable.autopulseyellow0011, R.drawable.autopulseyellow0012, R.drawable.autopulseyellow0013, R.drawable.autopulseyellow0014, R.drawable.autopulseyellow0015, R.drawable.autopulseyellow0016, R.drawable.autopulseyellow0017, R.drawable.autopulseyellow0018, R.drawable.autopulseyellow0019, R.drawable.autopulseyellow0020 };
    private Timer timer;
    private CountDownTimer countDownTimer;
    private SearchItem currentSearchItem;
    private boolean searchItemSelected = false;
    private boolean isBookingCar = false;
    private boolean isTripStart = false;
    private Reservation reservation;

    private float currentRotation = 0f;

    private MapSearchListAdapter mAdapter;
    private MapSearchListAdapter.OnItemActionListener mActionListener = new MapSearchListAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(SearchItem searchItem) {
            if(!searchItem.type.equals("none"))
                setSearchItemSelected(searchItem);
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

    @BindView(R.id.searchMapView)
    LinearLayout searchMapView;

    @BindView(R.id.bookingCarView)
    RelativeLayout bookingCarView;

    @BindView(R.id.userPinTextView)
    TextView userPinTextView;

    @BindView(R.id.bookingPlateTextView)
    TextView bookingPlateTextView;

    @BindView(R.id.bookingAddressTextView)
    TextView bookingAddressTextView;

    @BindView(R.id.expiringTimeTextView)
    TextView expiringTimeTextView;

    @BindView(R.id.expiringTimeView)
    ViewGroup expiringTimeView;

    @BindView(R.id.openButtonBookingView)
    ViewGroup openButtonBookingView;

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
        setSearchDefaultContent();
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

        if (mMapView!=null)
            mMapView.onDetach();
        mMapView=null;

        if(view != null)
            view.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);

        mPresenter.viewDestroy();

        if(timer != null) timer.cancel();
        if(countDownTimer != null) countDownTimer.cancel();
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

        //userLocation = new GeoPoint(45.538927, 9.168744); //TODO: remove

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

            if(!isTripStart && !isBookingCar) centerMap();

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

        //hasInit = true;
    }

    private void loadsCars(){
        mPresenter.loadCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), getFixMapRadius());
    }

    private void refreshCars(){

        int mapRadius = getMapRadius();

        if(mapRadius > 35000){

            if(poiMarkers != null)
                mMapView.getOverlays().remove(poiMarkers);

            if(poiCityMarkers != null)
                mMapView.getOverlays().remove(poiCityMarkers);

            poiCityMarkers = new FolderOverlay();

            final GeoPoint milanGeoPoint = new GeoPoint(45.465454, 9.186515);
            final GeoPoint romeGeoPoint = new GeoPoint(41.902783, 12.496365);
            final GeoPoint modenaGeoPoint = new GeoPoint(44.647128, 10.925226);
            final GeoPoint florenceGeoPoint = new GeoPoint(43.769560, 11.255813);

            //Milano
            showCityMarker(milanGeoPoint, R.drawable.ic_cluster_milan);

            //Roma
            showCityMarker(romeGeoPoint, R.drawable.ic_cluster_rome);

            //Modena
            showCityMarker(modenaGeoPoint, R.drawable.ic_cluster_modena);

            //Firenze
            showCityMarker(florenceGeoPoint, R.drawable.ic_cluster_firence);

            mMapView.getOverlays().add(poiCityMarkers);
            mMapView.invalidate();

        }else {

            try {
                mPresenter.refreshCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), getFixMapRadius());
            } catch (NullPointerException e) {
            }
        }

    }

    private void showCityMarker(final GeoPoint geoPoint, int clusterIcon){
        Marker markerCarCity = new Marker(mMapView);
        markerCarCity.setPosition(geoPoint);
        markerCarCity.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        markerCarCity.setIcon(getIconMarker(clusterIcon));
        markerCarCity.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {

                if(poiCityMarkers != null)
                    mMapView.getOverlays().remove(poiCityMarkers);

                mMapView.invalidate();

                mMapView.getController().setCenter(geoPoint);
                mMapView.getController().setZoom(11);
                mMapView.invalidate();

                /*try {
                    mPresenter.refreshCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), getMapRadius());
                } catch (NullPointerException e) {
                }*/
                //refreshCars();

                return true;
            }
        });

        poiCityMarkers.add(markerCarCity);

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

    private int getMapRadius(){
        IGeoPoint mapCenterLoc = mMapView.getMapCenter();

        Location locationA = new Location("point A");

        locationA.setLatitude(mapCenterLoc.getLatitude());
        locationA.setLongitude(mapCenterLoc.getLongitude());

        Location locationB = new Location("point B");

        locationB.setLatitude(mMapView.getProjection().getNorthEast().getLatitude());
        locationB.setLongitude(mMapView.getProjection().getNorthEast().getLongitude());

        int distance = (int) locationA.distanceTo(locationB);

        return distance;
    }

    private int getFixMapRadius(){
        return 700000;
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
        String address = getAddress(car.latitude, car.longitude);
        if(address.length() > 0)
            addressTextView.setText(address);

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

    private String getAddress(float latitude, float longitude){
        String address = "";

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if(!addresses.isEmpty())
                address = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
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
        /*Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);*/

        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

                    //searchEditText.requestFocus();
                    //setSearchResult();

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    searchEditText.setText(text);
                    initMapSearch();

                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                }
                break;
            }

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Marker
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Animazione del maker più vicino
    private void setMarkerAnimation(){

        if(carnextMarker != null || carbookingMarker != null) {
            if (timer != null) timer.cancel();

            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            if(getActivity() != null) {

                                int[] drawableAnimArray = null;

                                //Verifico se una prenotazione è attiva: il colore dell'animazione è giallo se c'è una prenotazione, altrimenti verde
                                if (isBookingCar || isTripStart)
                                    drawableAnimArray = drawableAnimYellowArray;
                                else drawableAnimArray = drawableAnimGreenArray;

                                //Ogni x millisecondi cambio il frame
                                if (isBookingCar || isTripStart) {

                                    if (carbookingMarker != null)
                                        carbookingMarker.setIcon(getIconMarker(drawableAnimArray[currentDrawable]));
                                    if (carnextMarker != null)
                                        carnextMarker.setIcon(getIconMarker(R.drawable.autopulse0001));
                                } else {
                                    if (carbookingMarker != null)
                                        carbookingMarker.setIcon(getIconMarker(R.drawable.autopulse0001));
                                    if (carnextMarker != null)
                                        carnextMarker.setIcon(getResources().getDrawable(drawableAnimArray[currentDrawable]));
                                }

                                mMapView.invalidate();

                                if (currentDrawable < drawableAnimArray.length - 1)
                                    currentDrawable++;
                                else currentDrawable = 0;
                            }

                        }
                    });
                }
            }, 100, 100);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Ricerca
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Listener: apertura / chiusura della tastiera
    private void setKeyboardListener(){

        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);

        if (view.getRootView().getHeight() - (r.bottom - r.top) > 500) { //Tastiera aperta

            //Setto l'altezza della view dei risultati di ricerca
            setSearchViewHeight();

            //Verifico se la view era precedentemente aperta
            if(!searchViewOpen) {

                //Mostro la view dei risultati
                setSearchResult();

                searchViewOpen = true;
            }
        } else { //Tastiera chiusa
            //Verifico se la view era precedentemente aperta
            if(searchViewOpen)
                clearSearch();

            searchEditText.clearFocus();
        }
    }

    private void setSearchResult(){

        //Mostro la view dei risultati
        searchMapResultView.setVisibility(View.VISIBLE);
    }


    //Metodo richiamato quando viene scritto qualcosa nella casella di ricerca
    private void initMapSearch(){

        currentSearchItem = null;

        String searchMapText = searchEditText.getText().toString();

        //Verifico prima di tutto che l'utente abbia scritto 3 caratteri. La ricerca parte nel momento in cui vengono digitati 3 caratteri
        if (searchMapText.length() > 2) {

            //Verifico se è una targa: (con pattern 2 lettere + 1 numero Es: AB1 ) è una targa e quindi cerchiamo tra le targhe, altrimenti cerchiamo l'indirizzo
            if(!StringUtils.isNumeric(searchMapText.substring(0))
                && !StringUtils.isNumeric(searchMapText.substring(1))
                  && StringUtils.isNumeric(searchMapText.substring(2))){
                mPresenter.findPlates(searchMapText);
            }else{
                mPresenter.findAddress(searchMapText);
            }

            searchRecyclerView.scrollToPosition(0);

        }else{ //Se i caratteri digitati sono meno di 3, ripulisco la lista

            mAdapter.setData(null);

            if(searchMapText.length() == 0) setSearchDefaultContent();
        }
    }

    //Setto l'altezza della view contente i risultati di una ricerca
    private void setSearchViewHeight(){

        //Prelevo l'altezza di una singola voce della lista
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = getResources().getDimension(R.dimen.search_item_height); // * (metrics.densityDpi / 160f)
        float itemHeight = Math.round(px);

        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);

        //Calcolo il numero di elementi che possono essere visualizzati all'interno dell'intefaccia senza che nessuno venga tagliato a livello visivo
        float totalHeight = r.height()- searchMapView.getHeight();
        int nItem = (int) (totalHeight / itemHeight) - 1;

        //Setto l'altezza della lista
        searchMapResultView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (itemHeight*nItem) + 5));
        searchRecyclerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (itemHeight*nItem)));
    }

    //Microfono
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

    private void setSearchItemSelected(SearchItem searchItem){
        hideSoftKeyboard();

        //Muovo la mappa
        mMapView.getController().setCenter(new GeoPoint(searchItem.latitude, searchItem.longitude));
        mMapView.getController().zoomTo(ZOOM_A);
        mMapView.getController().animateTo(new GeoPoint(searchItem.latitude, searchItem.longitude));

        //Se è una targa, apro il popup
        if(searchItem.type.equals("plate")){
            Car car = mPresenter.findPlateByID(searchItem.display_name);
            if(car != null)
                showPopupCar(car);
        }

        currentSearchItem = searchItem;

        if(searchItem.type.equals("address"))
            saveLastAndFavouriteSearch(searchItem);

        //Inserisco nella casella di testo il valore cercato
        searchItemSelected = true;
        searchEditText.setText(searchItem.display_name);
    }

    private void clearSearch(){
        //Nascondo la view dei risultati
        searchMapResultView.setVisibility(View.GONE);
        searchViewOpen = false;

        if(currentSearchItem == null) {
            //Pulisco la Edittext
            searchEditText.setText("");

            //Setto il contenuto di default
            setSearchDefaultContent();
        }
    }

    private void setSearchDefaultContent(){
        //Mostro preferiti + storisco nella view dei risultati
        mPresenter.getSearchItems("", getContext(), getActivity().getPreferences(MODE_PRIVATE));
    }

    //Salvo l'ultima ricerca fatta
    private void saveLastAndFavouriteSearch(SearchItem searchItem){
        //PreferencesDataSource aa = new PreferencesDataSource(getActivity().getSharedPreferences("aa", 0));
        mPresenter.saveSearchResultOnHistoric(getActivity().getPreferences(MODE_PRIVATE), searchItem);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Apri portiere
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void openDoors(){

        if(carSelected != null){
            if(userLocation != null){
                //Calcolo la distanza
                if(getDistance(carSelected) <= 1000000000){ //TODO: valore a 50
                    //Procediamo con le schermate successive
                    mPresenter.openDoor(carSelected, "open");
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Booking
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Metodo richiamato per prenotare una macchina selezionata
    private void bookingCar(){
        mPresenter.bookingCar(carSelected, getContext());
    }

    //Visualizzio le informazioni della prenotazione
    private void openViewBookingCar(){

        int pinUser = mPresenter.getUser().userInfo.pin;
        String plateBooking = carSelected.id;
        String addressBooking = getAddress(carSelected.latitude, carSelected.longitude);
        String timingBookin = "";


        if(reservation != null){

            long unixTime = System.currentTimeMillis() / 1000L;
            int diffTime = (int) (unixTime - reservation.timestamp_start);

            countDownTimer = new CountDownTimer((reservation.length - diffTime) * 1000, 1000) {

                public void onTick(long millisUntilFinished) {

                    int mn = (int) (millisUntilFinished / 1000 / 60);
                    int sec = (int) (millisUntilFinished / 1000 % 60);
                    String mnStr = (mn<10 ? "0" : "")+mn;
                    String secStr = (sec<10 ? "0" : "")+sec;
                    //timingBookin = mnStr+":"+secStr;

                    if(getActivity() != null)
                    expiringTimeTextView.setText(Html.fromHtml(String.format(getString(R.string.booking_expirationtime), mnStr+":"+secStr)));
                    else if(countDownTimer != null) countDownTimer.cancel();
                }

                public void onFinish() {}
            }.start();
        }
        //----

        //Popolo le informazioni
        //Pin utente
        userPinTextView.setText(String.format(getString(R.string.booking_userpin_label), ""+pinUser));
        bookingPlateTextView.setText(String.format(getString(R.string.booking_plate_label), plateBooking));
        bookingAddressTextView.setText(addressBooking);
        expiringTimeTextView.setText(Html.fromHtml(String.format(getString(R.string.booking_expirationtime), timingBookin)));

        //Apro le informazioni
        bookingCarView.setVisibility(View.VISIBLE);


        //Verifico se la corsa è iniziata: nel caso nascondo le informazioni non necessarie
        if(isTripStart){
            expiringTimeView.setVisibility(View.GONE);
            openButtonBookingView.setVisibility(View.GONE);
        }

        //refreshCars();

        for(Marker marker : poiMarkers.getItems()){
            if(marker.getTitle().equals(plateBooking)){
                carbookingMarker = marker;
            }
        }

    }

    private void deleteBooking(){
        //Chiedo conferma all'utente se vuole eliminare la prenotazione della macchina
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.booking_delete_alert),
                getString(R.string.ok),
                getString(R.string.cancel));
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
                mPresenter.deleteBookingCar(reservation.id);
            }
        });
    }

    private void closeViewBookingCar(){

        //Nascondo le informazioni della prenotazione cancellata
        bookingCarView.setVisibility(View.GONE);
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
        if(!searchItemSelected)
            initMapSearch();
        else searchItemSelected = false;
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
        mPresenter.refreshCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), getFixMapRadius());
    }

    @OnClick(R.id.closePopupButton)
    public void onClosePopup() {
        popupCarView.animate().translationY(popupCarView.getHeight());
    }

    @OnClick(R.id.openDoorButton)
    public void onOpenDoor(){
        onClosePopup();
        openDoors();
    }

    @OnClick(R.id.openDoorBookingButton)
    public void openDoorBookingButton(){
        onClosePopup();
        openDoors();
    }

    @OnClick(R.id.bookingCarButton)
    public void onBookingCar(){
        bookingCar();
    }

    @OnClick(deleteBookingButton)
    public void onDeleteBookingButton(){
        deleteBooking();
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

        //Marker array
        items = new ArrayList<OverlayItem>();

        if(poiMarkers != null)
            mMapView.getOverlays().remove(poiMarkers);

        if(poiCityMarkers != null)
            mMapView.getOverlays().remove(poiCityMarkers);

        poiMarkers = new RadiusMarkerClusterer(getActivity());

        boolean bookedCarFind = false;
        for(final Car car : carsList){
            //Verifico che la macchina sia in status = operative
            if(car.status.equals("operative")) {
                int icon_marker = R.drawable.ic_auto;

                //Verifico se la vettura è la più vicina oppure se è una vettura prenotata
                if(car.id.equals(carnext_id) || ((isBookingCar || isTripStart) && car.id.equals(carSelected.id))){
                    icon_marker = R.drawable.autopulse0001;
                }

                //Creo il marker
                Marker markerCar = new Marker(mMapView);
                markerCar.setPosition(new GeoPoint(car.latitude, car.longitude));
                markerCar.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                markerCar.setIcon(getIconMarker(icon_marker));
                markerCar.setTitle(car.id);
                markerCar.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker, MapView mapView) {

                        onTapMarker(car);
                        return true;
                    }
                });
                poiMarkers.add(markerCar);
                
                //OverlayItem overlayItem = new OverlayItem(car.id, "", new GeoPoint(car.latitude, car.longitude));
                //overlayItem.setMarker(getIconMarker(icon_marker));

                if(car.id.equals(carnext_id)){
                    carnextMarker = markerCar;
                }
                //Verifico se è attiva una prenotazione e se la targa dell'overley corrisponde a quella della macchina prenotata
                if(isBookingCar || isTripStart){
                    if(car.id.equals(carSelected.id)) {
                        carbookingMarker = markerCar;
                        bookedCarFind = true;
                    }
                }

                //items.add(overlayItem);

            }
        }

        //Se è attiva una prenotazione, ma la macchina non è presente tra i risultati restituiti dal server aggiungo la macchina alla lista
        if((isBookingCar || isTripStart) && !bookedCarFind){
            //OverlayItem overlayItem = new OverlayItem(carSelected.id, "", new GeoPoint(carSelected.latitude, carSelected.longitude));
            //overlayItem.setMarker(getIconMarker(R.drawable.autopulse0001));
            //carbookingMarker = overlayItem;

            //Creo il marker
            Marker markerCar = new Marker(mMapView);
            markerCar.setPosition(new GeoPoint(carSelected.latitude, carSelected.longitude));
            markerCar.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            markerCar.setIcon(getIconMarker(R.drawable.autopulse0001));
            markerCar.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {

                    onTapMarker(carSelected);
                    return true;
                }
            });
            poiMarkers.add(markerCar);
            carbookingMarker = markerCar;
        }


        //Clustering
        Drawable clusterIconD = getIconMarker(R.drawable.ic_cluster);
        Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();
        poiMarkers.setIcon(clusterIcon);
        poiMarkers.getTextPaint().setColor(ContextCompat.getColor(getContext(), R.color.darkpastelgreen));
        poiMarkers.getTextPaint().setTextSize(15 * getResources().getDisplayMetrics().density);
        poiMarkers.getTextPaint().setFakeBoldText(true);

        mMapView.getOverlays().add(poiMarkers);
        mMapView.invalidate();


        /*if(mOverlay != null)
            mOverlay.removeAllItems();

        mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(
                getActivity(), items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) { //Evento tap sul pin

                        //Verifico se è attiva una prenotazione
                        if(isBookingCar){
                            //Mostro un'alert di avviso
                            final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                                    getString(R.string.booking_bookedcar_alert),
                                    getString(R.string.ok),
                                    null);
                            cdd.show();
                            cdd.yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    cdd.dismissAlert();
                                }
                            });
                        }else
                            showPopupCar(carsList.get(index));
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                });*/
        //mOverlay.setFocusItemsOnTap(true);

        //mMapView.getOverlays().add(mOverlay);
        //mMapView.invalidate();




        setMarkerAnimation();


        //Stop sull'animazione del pulsante di refresh
        anim.cancel();

    }
    private void onTapMarker(Car car){
        //Verifico se è attiva una prenotazione
        if(isBookingCar || isTripStart){

            //Mostro un'alert di avviso
            final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                    getString(R.string.booking_bookedcar_alert),
                    getString(R.string.ok),
                    null);
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                }
            });
        }else {
            mMapView.getController().setCenter(new GeoPoint(car.latitude, car.longitude));
            mMapView.getController().setZoom(ZOOM_C);
            showPopupCar(car);
        }
    }

    @Override
    public void noCarsFound() {
        //Stop sull'animazione del pulsante di refresh
        anim.cancel();
    }

    @Override
    public void showSearchResult(List<SearchItem> searchItemList) {

        setSearchViewHeight();
        mAdapter.setData(searchItemList);

    }

    @Override
    public void showBookingCar(Reservation mReservation) {
        reservation = mReservation;

        onClosePopup();
        isBookingCar = true;
        openViewBookingCar();
    }

    @Override
    public void showConfirmDeletedCar(){

        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.booking_deleteconfirm_alert),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
                isBookingCar = false;
                closeViewBookingCar();
            }
        });

    }

    @Override
    public void showTripInfo(final Car car){

        isTripStart = true;
        carSelected = car;

        mMapView.getOverlays().remove(poiMarkers);

        //Aggiungo la macchina
        Marker markerCar = new Marker(mMapView);
        markerCar.setPosition(new GeoPoint(car.latitude, car.longitude));
        markerCar.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        markerCar.setIcon(getIconMarker(R.drawable.autopulse0001));
        markerCar.setTitle(car.id);
        markerCar.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {

                onTapMarker(car);
                return true;
            }
        });
        if(poiMarkers == null){
            poiMarkers = new RadiusMarkerClusterer(getActivity());
        }
        poiMarkers.add(markerCar);
        carbookingMarker = markerCar;

        mMapView.getOverlays().add(poiMarkers);


        mMapView.getController().setCenter(new GeoPoint(car.latitude, car.longitude));
        mMapView.getController().setZoom(ZOOM_C);
        mMapView.invalidate();

        setMarkerAnimation();

        openViewBookingCar();
    }

    @Override
    public void setTripInfo(Trip trip){

        isTripStart = true;

        carSelected = new Car(trip.id+"", trip.longitude, trip.latitude);
        openViewBookingCar();
    }

    @Override
    public void removeTripInfo(){
        isTripStart = false;
        closeViewBookingCar();
    }


    @Override
    public void showReservationInfo(final Car mCar, Reservation mReservation){

        isBookingCar = true;

        carSelected = mCar;
        reservation = mReservation;

        mMapView.getOverlays().remove(poiMarkers);

        //Aggiungo la macchina
        Marker markerCar = new Marker(mMapView);
        markerCar.setPosition(new GeoPoint(mCar.latitude, mCar.longitude));
        markerCar.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        markerCar.setIcon(getIconMarker(R.drawable.autopulse0001));
        markerCar.setTitle(mCar.id);
        markerCar.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {

                onTapMarker(mCar);
                return true;
            }
        });
        if(poiMarkers == null){
            poiMarkers = new RadiusMarkerClusterer(getActivity());
        }
        poiMarkers.add(markerCar);
        carbookingMarker = markerCar;

        mMapView.getOverlays().add(poiMarkers);


        mMapView.getController().setCenter(new GeoPoint(carSelected.latitude, carSelected.longitude));
        mMapView.getController().setZoom(ZOOM_C);


        mMapView.invalidate();

        setMarkerAnimation();

        onClosePopup();
        openViewBookingCar();
    }

    @Override
    public void setReservationInfo(Car mCar, Reservation mReservation){

        isBookingCar = true;

        carSelected = mCar;
        reservation = mReservation;

        openViewBookingCar();
    }

    @Override
    public void removeReservationInfo(){
        isBookingCar = false;
        closeViewBookingCar();
    }

    @Override
    public void openTripEnd(int timestamp){
        Navigator.launchTripEnd(this, timestamp);
    }
}
