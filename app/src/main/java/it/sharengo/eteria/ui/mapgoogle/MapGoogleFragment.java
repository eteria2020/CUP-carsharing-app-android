package it.sharengo.eteria.ui.mapgoogle;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidmapsextensions.ClusterGroup;
import com.androidmapsextensions.ClusterOptions;
import com.androidmapsextensions.ClusterOptionsProvider;
import com.androidmapsextensions.ClusteringSettings;
import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.Marker;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.androidmapsextensions.PolygonOptions;
import com.androidmapsextensions.PolylineOptions;
import com.example.x.circlelayout.CircleLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import it.handroix.map.HdxFragmentMapHelper;
import it.sharengo.eteria.App;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.Car;
import it.sharengo.eteria.data.models.City;
import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.data.models.KmlServerPolygon;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.Reservation;
import it.sharengo.eteria.data.models.ResponseGoogleRoutes;
import it.sharengo.eteria.data.models.SearchItem;
import it.sharengo.eteria.data.models.Trip;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.assistance.AssistanceActivity;
import it.sharengo.eteria.ui.base.activities.BaseActivity;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;
import it.sharengo.eteria.ui.base.map.BaseMapFragment;
import it.sharengo.eteria.ui.components.CustomButton;
import it.sharengo.eteria.ui.components.CustomDialogClass;
import it.sharengo.eteria.ui.mapgoogle.CircleLayout.MyCircleLayoutAdapter;
import it.sharengo.eteria.ui.menu.MenuFragment;
import it.sharengo.eteria.utils.ImageUtils;
import it.sharengo.eteria.utils.ResourceProvider;
import it.sharengo.eteria.utils.StringsUtils;

import static android.content.Context.MODE_PRIVATE;


public class MapGoogleFragment extends BaseMapFragment<MapGooglePresenter> implements MapGoogleMvpView, OnMapReadyCallback, LocationListener {

    private static final String TAG = MapGoogleFragment.class.getSimpleName();


    public static final String ARG_TYPE = "ARG_TYPE";
    private int type = 0;

    private View view;

    //TODO Remove
    private boolean test_corsa = false;

    /* General */
    private boolean hasInit;
    private Car carPreSelected;

    /* Location */
    Location userLocation;
    private boolean prevLocationDisabled;

    /* Animazioni */
    private Timer timer;

    /* Feeds */
    private boolean showCarsWithFeeds;
    private Feed feedSelected;

    /* Search */
    private final int SPEECH_RECOGNITION_CODE = 1;
    private boolean searchViewOpen = false;
    private SearchItem currentSearchItem;
    private boolean searchItemSelected = false;
    private Timer timerEditText;
    private final long DELAY = 500;
    private MapSearchListAdapter mAdapter;
    private MapSearchListAdapter.OnItemActionListener mActionListener = new MapSearchListAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(SearchItem searchItem) {
            if(!searchItem.type.equals("none"))
                setSearchItemSelected(searchItem);
        }
    };
    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            setKeyboardListener();
        }
    };

    /* Menu circolare */
    private MyCircleLayoutAdapter ad;
    private MyCircleLayoutAdapter.OnItemActionListener mActionCircleListener = new MyCircleLayoutAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(int index) {
            onCircleMenuClick(index);
        }
    };
    private RotateAnimation anim;

    /* Booking - Trip */
    private Timer timerTripDuration;
    private CountDownTimer countDownTimer;
    private boolean isBookingCar;
    private boolean isTripStart;
    private boolean isTripParked;
    private Reservation reservation;
    private int tripTimestampStart;
    private float co2;
    private boolean openDoorFromBooking;
    private View.OnClickListener mNotificationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Navigator.launchTripEnd(MapGoogleFragment.this, co2);
        }
    };

    /* Map */
    private int mapRadius;
    private LatLng defaultLocation;
    private com.androidmapsextensions.Marker userMarker;
    private List<com.androidmapsextensions.Marker> poiMarkers;
    private List<MarkerOptions> poiMarkersToAdd;
    private List<com.androidmapsextensions.Marker> feedsMarker;
    private List<com.androidmapsextensions.Marker> poiCityMarkers;
    private List<com.androidmapsextensions.Polygon> polygonsMaps;
    private ClusterManager<ClusterItem> mClusterManager;
    private String carnext_id;
    private Car carNext;
    private Car carSelected;
    private Car carBooked;
    private Car carWalkingNavigation;
    private com.androidmapsextensions.Marker carnextMarker, carbookingMarker, carNextCluster;
    private MarkerOptions carNextClusterOptions;
    private int currentDrawable = 0; //frame dell'animazione della macchiana più vicina
    private int NUM_ANIM = 40; //46
    private List<BitmapDescriptor> drawableAnimGreenArray;
    private List<BitmapDescriptor> drawableAnimYellowArray;
    private BitmapDescriptor bitmapAuto;
    private BitmapDescriptor bitmapUser;
    private float currentRotation;
    private boolean cityClusterVisible;
    boolean findNextCarIntoCluster;
    private boolean unparkAction;
    private Location walkingDestination;
    private PolylineOptions polyWalkingOptions;
    private com.androidmapsextensions.Polyline polyWalking;
    private Timer timerUpadateMap;

    @BindView(R.id.mapView)
    FrameLayout mMapContainer;

    @BindView(R.id.searchEditText)
    EditText searchEditText;

    @BindView(R.id.searchMapResultView)
    ViewGroup searchMapResultView;

    @BindView(R.id.searchRecyclerView)
    RecyclerView searchRecyclerView;

    @BindView(R.id.searchMapView)
    LinearLayout searchMapView;

    @BindView(R.id.roundMenuMapView)
    ViewGroup roundMenuMapView;

    @BindView(R.id.roundMenuFeedsView)
    ViewGroup roundMenuFeedsView;

    @BindView(R.id.circularLayout)
    CircleLayout circularLayout;

    @BindView(R.id.centerMapButton)
    ImageView centerMapButton;

    /*@BindView(R.id.orientationMapButton)
    ImageView orientationMapButton;

    @BindView(R.id.orientationMapButtonView)
    ViewGroup orientationMapButtonView;*/

    @BindView(R.id.assistanceButton)
    ImageView assistanceButton;

    @BindView(R.id.assistanceButtonView)
    ViewGroup assistanceButtonView;

    @BindView(R.id.centerMapButtonView)
    ViewGroup centerMapButtonView;

    @BindView(R.id.refreshMapButtonView)
    ViewGroup refreshMapButtonView;

    @BindView(R.id.carFeedMapButtonView)
    ViewGroup carFeedMapButtonView;

    @BindView(R.id.refreshMapButton)
    ImageView refreshMapButton;

    @BindView(R.id.carFeedMapButton)
    ImageView carFeedMapButton;

    @BindView(R.id.popupCarView)
    View popupCarView;

    @BindView(R.id.plateTextView)
    TextView plateTextView;

    @BindView(R.id.autonomyTextView)
    TextView autonomyTextView;

    @BindView(R.id.addressTextView)
    TextView addressTextView;

    @BindView(R.id.timeTextView)
    TextView timeTextView;

    @BindView(R.id.timeView)
    ViewGroup timeView;

    @BindView(R.id.closestcarView)
    ViewGroup closestcarView;

    @BindView(R.id.distanceTextView)
    TextView distanceTextView;

    @BindView(R.id.distanceView)
    ViewGroup distanceView;

    @BindView(R.id.expiringTimeTextView)
    TextView expiringTimeTextView;

    @BindView(R.id.tripDurationTextView)
    TextView tripDurationTextView;

    @BindView(R.id.userPinTextView)
    TextView userPinTextView;

    @BindView(R.id.bookingPlateTextView)
    TextView bookingPlateTextView;

    @BindView(R.id.bookingTitleTextView)
    TextView bookingTitleTextView;

    @BindView(R.id.bookingAddressTextView)
    TextView bookingAddressTextView;

    @BindView(R.id.timeIconImageView)
    ImageView timeIconImageView;

    @BindView(R.id.bookingCarView)
    RelativeLayout bookingCarView;

    @BindView(R.id.openButtonBookingView)
    ViewGroup openButtonBookingView;

    @BindView(R.id.feedImageView)
    ImageView feedImageView;

    @BindView(R.id.feedAdvantageTextView)
    TextView feedAdvantageTextView;

    @BindView(R.id.feedTriangleView)
    ViewGroup feedTriangleView;

    @BindView(R.id.feedTriangleImageView)
    ImageView feedTriangleImageView;

    @BindView(R.id.feedOverlayView)
    View feedOverlayView;

    @BindView(R.id.feedIconImageView)
    ImageView feedIconImageView;

    @BindView(R.id.feedIntersImageView)
    ImageView feedIntersImageView;

    @BindView(R.id.feedLaunchTitleTextView)
    TextView feedLaunchTitleTextView;

    @BindView(R.id.feedDateTextView)
    TextView feedDateTextView;

    @BindView(R.id.feedTitleTextView)
    TextView feedTitleTextView;

    @BindView(R.id.feedLocationTextView)
    TextView feedLocationTextView;

    @BindView(R.id.feedAdvantageBottomTextView)
    TextView feedAdvantageBottomTextView;

    @BindView(R.id.popupFeedView)
    View popupFeedView;

    @BindView(R.id.deleteBookingButton)
    Button deleteBookingButton;

    @BindView(R.id.openDoorBookingButton)
    Button openDoorBookingButton;

    @BindView(R.id.openDoorButton)
    CustomButton openDoorButton;

    @BindView(R.id.closestcarTextView)
    TextView closestcarTextView;

    @BindView(R.id.cancelButtonSearch)
    ImageView cancelButtonSearch;


    public static MapGoogleFragment newInstance(int type) {
        MapGoogleFragment fragment = new MapGoogleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getMvpFragmentComponent(savedInstanceState).inject(this);

        if(getArguments() != null){
            type = getArguments().getInt(ARG_TYPE);
            if(type == Navigator.REQUEST_MAP_FEEDS) mPresenter.isFeeds = true;
            else mPresenter.isFeeds = false;
        }

        //Init
        hasInit = false;
        defaultLocation = new LatLng(41.931543, 12.503420);
        mAdapter = new MapSearchListAdapter(mActionListener);
        isBookingCar = false;
        isTripStart = false;
        isTripParked = false;
        unparkAction = false;
        prevLocationDisabled = false;
        mapRadius = 0;
        tripTimestampStart = 0;
        co2 = 0f;
        openDoorFromBooking = false;
        currentRotation = 0f;
        cityClusterVisible = false;
        findNextCarIntoCluster = false;
        walkingDestination = new Location("destination");
        /*drawableAnimGreenArray = new ArrayList<>();
        drawableAnimYellowArray = new ArrayList<>();
        for(int i = 0; i <= NUM_ANIM; i++){
            if(i < 10) {
                drawableAnimGreenArray.add("autopulse000" + i);
                drawableAnimYellowArray.add("autopulseyellow000" + i);
            }else {
                drawableAnimGreenArray.add("autopulse00" + i);
                drawableAnimYellowArray.add("autopulseyellow00" + i);
            }
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_google, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapHelper = HdxFragmentMapHelper.newInstance(getActivity(), this);
        mMapHelper.setupMap(mMapContainer, this, savedInstanceState);

        //Setup ricerca
        setupSearch();

        //Setup animazione menu circolare
        setupCircleMenu();

        showCarsWithFeeds = false;

        //Imposto il listener sull'apertura della tastiera: se appare la tastiera devo aprire la ricerca
        view.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

        if(!mPresenter.isAuth()){ final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                getString(R.string.popup_home),
                getString(R.string.popup_login),
                getString(R.string.popup_registrazione));
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                    Navigator.launchLogin(MapGoogleFragment.this, Navigator.REQUEST_LOGIN_START);
                   /*Intent intent = UserAreaActivity.getCallingIntent(UserAreaFragment);
                   HomeFragment.this.startActivity(intent);*/
                }
            });

            cdd.no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                    Navigator.launchSlideshow(MapGoogleFragment.this);
                   /*Intent intent = UserAreaActivity.getCallingIntent(UserAreaFragment);
                   HomeFragment.this.startActivity(intent);*/
                }
            });
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();


        if(view != null)
            view.getViewTreeObserver().removeOnGlobalLayoutListener(layoutListener);

        mPresenter.viewDestroy();

        if(timer != null) timer.cancel();
        if(timerTripDuration != null) timerTripDuration.cancel();
        if(countDownTimer != null) countDownTimer.cancel();
        if(timerEditText != null) timerEditText.cancel();
        if(timerUpadateMap != null) timerUpadateMap.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        mPresenter.mAppRepository.selectMenuItem(MenuItem.Section.BOOKING);
        if(!isConnected) {
            onProviderDisabled("");
        }


        updateLocation();

    }

    @Override
    public void onPause(){
        super.onPause();
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lm.removeUpdates(this);

        mPresenter.viewOnPause();

        removeTripInfo();
        removeReservationInfo();
    }

    private void updateLocation(){
        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                return;
            }

        }
        catch (Exception ex){

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Map listener
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set every information on instance of Google Maps.
     * Set cluster, camera, rotation and refresh car available.
     *
     * @param  googleMap instance of Google Maps.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);



        mMap.setIndoorEnabled(false);


        drawableAnimGreenArray = new ArrayList<>();
        drawableAnimYellowArray = new ArrayList<>();
        int sizeMarkerAnim = (int) (177 * getResources().getDisplayMetrics().density);
        for(int i = 0; i <= NUM_ANIM; i++){
            if(i < 10) {
                drawableAnimGreenArray.add(getBitmapDescriptor(resizeMapIcons("autopulse000" + i, sizeMarkerAnim, sizeMarkerAnim)));
                drawableAnimYellowArray.add(getBitmapDescriptor(resizeMapIcons("autopulseyellow000" + i, sizeMarkerAnim, sizeMarkerAnim)));
            }else {
                drawableAnimGreenArray.add(getBitmapDescriptor(resizeMapIcons("autopulse00" + i, sizeMarkerAnim, sizeMarkerAnim)));
                drawableAnimYellowArray.add(getBitmapDescriptor(resizeMapIcons("autopulseyellow00" + i, sizeMarkerAnim, sizeMarkerAnim)));
            }
        }


        bitmapAuto = getBitmapDescriptor(resizeMapIcons("ic_auto", (int) (39 * getResources().getDisplayMetrics().density), (int) (48 * getResources().getDisplayMetrics().density)));
        bitmapUser = getBitmapDescriptor(R.drawable.ic_user);


        setMapReady();

    }

    private void setMapReady(){
        mPresenter.onMapIsReady();

        //Clustering
        mMap.setClustering(new ClusteringSettings().clusterOptionsProvider(new DemoClusterOptionsProvider(getResources())));

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if(getActivity() != null) {

                    /*CameraPosition oldPos = mMap.getCameraPosition();
                    setRotationButton(oldPos.bearing);*/

                    refreshCars();
                }

            }
        });

        //Avvio il timer che controlla se c'è una corsa attiva e nel caso centro la mappa sull'utente
        timerUpadateMap = new Timer();

        timerUpadateMap.schedule(new TimerTask() {
            @Override
            public void run() {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(isTripStart && !isTripParked && mMap != null && userLocation != null){
                                moveMapCameraToPoitWithZoom(userLocation.getLatitude() + 0.0002, userLocation.getLongitude(), 19);
                            }

                        }
                    });
                }
            }
        }, 1000, 5000);
    }

    /**
     * Change user position if needed.
     *
     * @param  location new location of user.
     */
    @Override
    public void onNewLocation(Location location) {
        super.onNewLocation(location);

        locationChange(location);

    }

    /**
     * Send "message" location unvailable.
     */
    @Override
    public void onLocationUnavailable() {
        super.onLocationUnavailable();
        if(mMap != null)
            providerDisabled();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Location listener
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * When location changed set new camera and center on map.
     *
     * @param  location  new location of user
     */
    @Override
    public void onLocationChanged(Location location) {
        //mPresenter.onLocationIsReady(location.getLatitude(), location.getLongitude());

        locationChange(location);

        if(mMap != null && prevLocationDisabled && !isTripStart && !isBookingCar && userLocation != null){
            moveMapCameraTo(location.getLatitude(), location.getLongitude());
        }

        enabledCenterMap(true);

        //markerUser
        if(mMap != null){
            if(!isTripStart || (isTripStart && getMapRadius() < 35000) || (isTripStart && isTripParked)){
                drawUserMarker();
            }
        }

        prevLocationDisabled = false;

    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

        //updateLocation();

        prevLocationDisabled = true;

        if(!isTripStart && !isBookingCar){
            if(userLocation != null && mMap != null)
                moveMapCameraTo(userLocation.getLatitude(), userLocation.getLongitude());
        }

        enabledCenterMap(true);
    }

    @Override
    public void onProviderDisabled(String s) {

        prevLocationDisabled = false;

        if(mMap != null)
            providerDisabled();

        enabledCenterMap(false);

    }

    private void providerDisabled(){

        userLocation = null;
        //moveMapCameraToDefaultLocation();

        carnextMarker = null;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                //moveMapCameraToPoitWithZoom(defaultLocation.latitude, defaultLocation.longitude, 5);
                //refreshCars();
            }
        }, 100);
        //enabledCenterMap(false);

        if(!hasInit) {
            moveMapCameraToPoitWithZoom(defaultLocation.latitude, defaultLocation.longitude, 5);
        }

        if(carPreSelected != null){
            showPopupCar(carPreSelected);
            moveMapCameraToPoitWithZoom((double) carPreSelected.latitude, (double) carPreSelected.longitude, 19);
        }

        hasInit = true;

        if(userMarker != null) userMarker.remove();
    }

    private void locationChange(Location location){

        mPresenter.userLat = (float) location.getLatitude();
        mPresenter.userLon = (float) location.getLongitude();

        userLocation = location;

        //TODO Coor
        //userLocation.setLatitude(41.909917);
        //userLocation.setLongitude(12.456426); //Milano 45.510349, 9.093254 - Milano 2 45.464116, 9.191425 - Roma 41.895514, 12.486259    Vinovo 44.975330, 7.617876


        enabledCenterMap(true);

        //First time
        if (!hasInit){

            if(mMap != null)
                moveMapCameraToPoitWithZoom((double) userLocation.getLatitude(), (double) userLocation.getLongitude(), 17);


            if(mMap != null)
                refreshCars();
        }

        hasInit = true;

        if(carPreSelected != null){

            if(mMap != null)
                moveMapCameraToPoitWithZoom((double) carPreSelected.latitude, (double) carPreSelected.longitude, 19);

            showPopupCar(carPreSelected);

        }

        mPresenter.loadPlatesCached();

        //Aggiorno la Walk Navigation
        if(mMap != null && getMapRadius() < 35000){
            getWalkingNavigation();
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Search
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupSearch(){
        final LinearLayoutManager lm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        searchRecyclerView.setLayoutManager(lm);
        searchRecyclerView.setAdapter(mAdapter);
        timerEditText = new Timer();
        setSearchDefaultContent();
    }

    //Listener: apertura / chiusura della tastiera
    private void setKeyboardListener(){

        Rect r = new Rect();
        view.getWindowVisibleDisplayFrame(r);

        if (view.getRootView().getHeight() - (r.bottom - r.top) > 500) { //Tastiera aperta

            //Setto l'altezza della view dei risultati di ricerca
            setSearchViewHeight();

            searchEditText.requestFocus();

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
                //mPresenter.findAddress(searchMapText);
                Location placeLocation = new Location("place");
                placeLocation.setLatitude(41.931543);
                placeLocation.setLongitude(12.503420);
                if(userLocation != null){
                    placeLocation.setLatitude(userLocation.getLatitude());
                    placeLocation.setLongitude(userLocation.getLongitude());
                }
                mPresenter.searchPlace(getActivity(), searchMapText, placeLocation, mPresenter.mAppRepository.getLang());

                /*String placeUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurants+in+Sydney&key=AIzaSyAnVjGP9ZCkSkBVkrX-5SBdmNW9AwE_Gew";
                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, placeUrl, null, new com.android.volley.Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                //parseKml(context, response);
                                Log.w("GOOGLE PLACES",": "+response);
                            }
                        }, new com.android.volley.Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.w("GOOGLE PLACES",": "+error);
                            }
                        }) {
                };

                RequestQueue queue = Volley.newRequestQueue(getActivity());
                queue.add(jsObjRequest);*/
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchRecyclerView.scrollToPosition(0);
                }
            });


        }else{ //Se i caratteri digitati sono meno di 3, ripulisco la lista

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mAdapter.setData(null);

                }
            });


            if(searchMapText.length() == 0) setSearchDefaultContent();
        }
    }

    private void setSearchItemSelected(SearchItem searchItem){
        hideSoftKeyboard();

        //Muovo la mappa
        moveMapCameraToPoitWithZoom((double) searchItem.latitude, (double) searchItem.longitude, 19);

        //Se è una targa, apro il popup
        if(searchItem.type.equals("plate")){
            Car car = mPresenter.findPlateByID(searchItem.display_name);
            if(car != null)
                showPopupCar(car);
        }

        currentSearchItem = searchItem;

        //Salvo la ricerca appena effettuata solo se sono un utente loggato
        if(searchItem.type.equals("address") && mPresenter.isAuth())
            saveLastAndFavouriteSearch(searchItem);

        //Inserisco nella casella di testo il valore cercato
        searchItemSelected = true;
        searchEditText.setText(searchItem.display_name);
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

        //Mostro preferiti + storisco nella view dei risultati (solo se l'utente è loggato)
        if(mPresenter.isAuth())
            mPresenter.getSearchItems("", getContext(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
    }

    //Salvo l'ultima ricerca fatta
    private void saveLastAndFavouriteSearch(SearchItem searchItem){
        mPresenter.saveSearchResultOnHistoric(getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE), searchItem);
    }

    //Microfono
    private void startSpeechToText() {

        InputMethodManager imm = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

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

    //Callback for speech recognition activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == getActivity().RESULT_OK && null != data) {

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
    //                                              Menu circolare
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setupCircleMenu(){

        //Rotate animation - refresh button
        anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);

        //Adapter
        ad = new MyCircleLayoutAdapter(mActionCircleListener);
        ad.animationRefresh = true;

        //Riposiziono i pulsanti del menu circolare in base alla modalità
        if(mPresenter.isFeeds){
            roundMenuMapView.setVisibility(View.GONE);
            roundMenuFeedsView.setVisibility(View.VISIBLE);


            ad.add(R.drawable.ic_assistenza_nero);
            ad.add(R.drawable.ic_center);
            ad.add(R.drawable.ic_referesh);
            ad.add(R.drawable.ic_cars);

            ad.add(R.drawable.ic_cars);
            ad.add(R.drawable.ic_referesh);
            ad.add(R.drawable.ic_center);
            ad.add(R.drawable.ic_assistenza_nero);
            ad.add(R.drawable.ic_cars);
            ad.add(R.drawable.ic_referesh);
            ad.add(R.drawable.ic_center);
            ad.add(R.drawable.ic_assistenza_nero);
            ad.add(R.drawable.ic_cars);
            ad.add(R.drawable.ic_referesh);
            ad.add(R.drawable.ic_center);
            ad.add(R.drawable.ic_assistenza_nero);


            circularLayout.setAdapter(ad);
            circularLayout.setChildrenCount(16);
            circularLayout.setOffsetY(-13);
            circularLayout.setOffsetX(86);
            circularLayout.setRadius(60);
            circularLayout.setChildrenPinned(true);

        }else{
            roundMenuMapView.setVisibility(View.VISIBLE);
            roundMenuFeedsView.setVisibility(View.GONE);

            //orientationMapButtonView.setTranslationX(-157.0f);
            //orientationMapButtonView.setTranslationY(12.0f);

            //centerMapButtonView.setTranslationX(-100.0f);
            //centerMapButtonView.setTranslationY(65.0f);

            //refreshMapButtonView.setTranslationX(-65.0f);
            //refreshMapButtonView.setTranslationY(100.0f);

            //carFeedMapButtonView.setTranslationX(-20.0f);
            //carFeedMapButtonView.setTranslationY(100.0f);
        }
    }
    private void onCircleMenuClick(int i){
        switch (i){
            case 0: //Help
                //TODO Inserire funzione assistance
                //onOrentationMap();
                launchAssistanceMap();
                break;
            case 1: //Center
                onCenterMap();
                break;
            case 2: //Refresh
                onRefreshMap();
                ad.animationRefresh = true;
                circularLayout.init();
                break;
            case 3: //Car
                onShowCarOnMapClick();
                if(!isBookingCar && !isTripStart) {
                    if (ad.carAlpha) ad.carAlpha = false;
                    else ad.carAlpha = true;
                }
                circularLayout.init();
                break;
        }
    }

    private void refreshMap(){
        if(getMapRadius() < 35000) {

            refreshMapButton.startAnimation(anim);

            float user_lat = 0;
            float user_lon = 0;
            if(userLocation != null){
                user_lat = (float) userLocation.getLatitude();
                user_lon = (float) userLocation.getLongitude();
            }
            mPresenter.refreshCars(getActivity(), (float) mMap.getCameraPosition().target.latitude, (float) mMap.getCameraPosition().target.longitude, user_lat, user_lon, getFixMapRadius(), mMap.getProjection().getVisibleRegion().latLngBounds, true);
        }
    }

    /*private void setRotationButton(float rotation){

        if(mPresenter.isFeeds){
            ad.rotation = -rotation;
            circularLayout.init();
        }
        orientationMapButton.setRotation(-rotation);
    }*/


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mappa
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void drawUserMarker(){
        if(mMap != null) {
            if (userMarker == null && userLocation != null) {
                userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude())));
                userMarker.setClusterGroup(ClusterGroup.NOT_CLUSTERED);
                userMarker.setZIndex(2);
            }


            if (userLocation != null && userMarker != null) {

                try {
                    if (userLocation != null && userMarker != null) {
                        userMarker.setPosition(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
                    }
                } catch (NullPointerException e) {
                }

                if (!isTripStart) {
                    try {
                        userMarker.setIcon(getBitmapDescriptor(R.drawable.ic_user));
                    }catch (NullPointerException e){} catch (IllegalStateException e){}
                }

            }
        }
    }

    private void refreshCars(){

        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            if(refreshMapButton != null) refreshMapButton.startAnimation(anim);
        }

        //drawUserMarker();

        mapRadius = getMapRadius();

        if(mapRadius > 35000){

            carnextMarker = null;


            if(!isTripStart || (isTripStart && isTripParked)) drawUserMarker();
            else if(userMarker != null){
                userMarker.remove();
                userMarker = null;
            }

            if(poiMarkers != null)
                removeMarkers(poiMarkers);

            if(feedsMarker != null)
                removeMarkers(feedsMarker);

            if(poiCityMarkers != null)
                removeMarkers(poiCityMarkers);

            poiCityMarkers = new ArrayList<>();

            mPresenter.loadCity(getActivity());

            if(polygonsMaps != null){
                removePolygons(polygonsMaps);
                cityClusterVisible = false;
            }

            if(carNextCluster != null)
            {
                carNextCluster.remove();
                carNextCluster = null;
            }

            if(polyWalking != null){
                polyWalking.setVisible(false);
            }

        }else {

            drawUserMarker();

            if(poiCityMarkers != null)  removeMarkers(poiCityMarkers);

            if(getMapCenter().longitude > 0) {
                try {
                    float user_lat = 0;
                    float user_lon = 0;
                    if(userLocation != null){
                        user_lat = (float) userLocation.getLatitude();
                        user_lon = (float) userLocation.getLongitude();
                    }
                    mPresenter.refreshCars(getActivity(), (float) getMapCenter().latitude, (float) getMapCenter().longitude, user_lat, user_lon, getFixMapRadius(), mMap.getProjection().getVisibleRegion().latLngBounds, false);
                } catch (NullPointerException e) {
                }
            }

            if(!cityClusterVisible){
                try {
                    mPresenter.loadKml(getContext());
                }catch (NullPointerException e){}
                cityClusterVisible = true;
            }

            //Aggiorno la Walk Navigation
            if(!isTripStart || (isTripStart && carBooked != null && carBooked.parking)){
                if(polyWalking != null){
                    polyWalking.setVisible(true);
                }
            }
        }

    }

    //Rimuovo un gruppo di marker dalla mappa
    private void removeMarkers(List<com.androidmapsextensions.Marker> markerList) {
        if(markerList != null) {
            for (com.androidmapsextensions.Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
        }
    }

    //Rimuovo un gruppo di poligoni dalla mappa
    private void removePolygons(List<com.androidmapsextensions.Polygon> polygons) {
        if(polygons != null) {
            for (com.androidmapsextensions.Polygon polygon : polygons) {
                polygon.remove();
            }
            polygons.clear();
        }
    }

    //Metodo richiamato quando viene eseguito il tap su un marker presente nella mappa
    @Override
    public boolean onMarkerClick(com.androidmapsextensions.Marker marker) {

        if(marker != null && marker.getData() != null) {

            //City
            if (marker.getData().getClass().equals(City.class)) {

                if (poiCityMarkers != null)
                    removeMarkers(poiCityMarkers);

                moveMapCameraToPoitWithZoom(marker.getPosition().latitude, marker.getPosition().longitude, 12);

            }

            //Car
            if (marker.getData().getClass().equals(Car.class)) {

                onTapMarker((Car) marker.getData());

            }

            //Feed
            if (marker.getData().getClass().equals(Feed.class)) {

                onTapFeedMarker((Feed) marker.getData());

            }
        }else if(marker != null && marker.isCluster()){ //Cluster

            if(marker != null)
            zoomCarmeraIn(marker.getPosition().latitude, marker.getPosition().longitude);
        }


        //Chiudo la ricerca
        hideSoftKeyboard();

        return true;
    }


    //Metodo per recuperare il raggio della mappa
    private int getMapRadius(){

        LatLng mapCenterLoc = getMapCenter();

        Location locationA = new Location("point A");

        locationA.setLatitude(mapCenterLoc.latitude);
        locationA.setLongitude(mapCenterLoc.longitude);

        Location locationB = new Location("point B");

        locationB.setLatitude(mMap.getProjection().getVisibleRegion().latLngBounds.northeast.latitude);
        locationB.setLongitude(mMap.getProjection().getVisibleRegion().latLngBounds.northeast.longitude);

        int distance = (int) locationA.distanceTo(locationB);

        return distance;
    }

    private int getFixMapRadius(){
        //return 700000;
        return getMapRadius();
    }
    //Metodo per aprire pagina assistenza
    public static void launchAssistance(Fragment fragment) {
        Intent intent = AssistanceActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    //Metodo per resettare l'orientamento della mappa se l'utente l'ha ruotata
    /*private void orientationMap(){

        //Navigator.launchAssistance(MapGoogleFragment.this);
        if(!isAdded()) return;

        if(mMap != null) {

            CameraPosition oldPos = mMap.getCameraPosition();
            CameraPosition pos = CameraPosition.builder(oldPos).bearing(0.0f).build();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

            orientationMapButton.setRotation(0.0f);
        }

        //setRotationButton(0.0f);
    }*/

    //Recupero il centro della mappa
    private LatLng getMapCenter(){
        return mMap.getCameraPosition().target;
    }

    //Abilito / disabilito pulsante per centrare la mappa
    private void enabledCenterMap(boolean enable){

        if(enable && centerMapButton != null){

            centerMapButton.setAlpha(1.0f);
            if(mPresenter.isFeeds && ad != null) ad.centerAlpha = false;

        }else if(centerMapButton != null){

            centerMapButton.setAlpha(.4f);
            if(mPresenter.isFeeds && ad != null) ad.centerAlpha = true;

        }

        if(mPresenter.isFeeds){
            circularLayout.init();
        }
    }

    //Metodo richiamato per centrare la mappa. Se la localizzazione non è attiva, viene avvisato l'utente
    private void centerMap(){

        if(userLocation != null) {
            moveMapCameraToPoitWithZoom(userLocation.getLatitude(), userLocation.getLongitude(), 17);
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

    //Apre i setting del dispositivo per permettere di attivare il gps
    private void openSettings(){
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    //Disegna sulla mappa i marker delle città (Milano, Roma, ecc...)
    private void drawCityMarkerOnMap(List<City> cityList){
        for(City cA : cityList){

            /*
            * Bitmap base = BitmapFactory.decodeResource(resources, res[i]);
            * Bitmap bitmap = base.copy(Bitmap.Config.ARGB_8888, true);
            * BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
            * */

            com.androidmapsextensions.Marker markerCity = mMap.addMarker(new MarkerOptions().position(new LatLng(cA.informations.address.latitude, cA.informations.address.longitude)));
            //markerCity.setIcon(getBitmapDescriptor(R.drawable.ic_cluster));
            markerCity.setClusterGroup(ClusterGroup.NOT_CLUSTERED);
            markerCity.setData(cA);
            markerCity.setAnchor(0.5f, 0.5f);

            poiCityMarkers.add(markerCity);


            //Disegno i componenti grafici
            final com.androidmapsextensions.Marker finalMarkerCarCity = markerCity;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    if(getActivity() != null) {
                        try {
                            finalMarkerCarCity.setIcon(getBitmapDescriptor(makeBasicMarker(bitmap)));
                        } catch (NullPointerException e) {
                        }
                    }

                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };
            Picasso.with(getActivity()).load(cA.media.images.icon.uri).into(target);

            markerCity.setTag(target);
        }

        anim.cancel();
        if(mPresenter.isFeeds){
            ad.animationRefresh = false;
            circularLayout.init();
        }
    }

    //Metodo per predisporre i pin che dovranno poi essere disegnati sulla mappa
    private void showCarsOnMap(List<Car> carsList){

        //Rimuovo i marker dalla mappa
        /*if(poiMarkers != null)
            removeMarkers(poiMarkers);*/

        if(poiCityMarkers != null)
            removeMarkers(poiCityMarkers);


        poiMarkersToAdd = new ArrayList<>();



        for(final Car car : carsList){
            //Verifico che la macchina sia in status = operative
            //if(car.status.equals("operative")) {
                int icon_marker = R.drawable.ic_auto;


                //Verifico se la vettura è la più vicina oppure se è una vettura prenotata
                if(car.id.equals(carnext_id) || ((isBookingCar || isTripStart) && car.id.equals(carBooked.id))){
                    icon_marker = R.drawable.ic_auto;
                }

                //Creo il marker
                MarkerOptions markerCar = new MarkerOptions().position(new LatLng(car.latitude, car.longitude));
                if(car.bonus != null&& !car.bonus.isEmpty() && car.bonus.get(0).status && car.bonus.get(0).type.equals("nouse")){ //Macchina con minuti free
                    markerCar.icon(getBitmapDescriptor(makeFreeMarker(ResourceProvider.getDrawable(getActivity(), R.drawable.ic_auto_free), String.valueOf(car.bonus.get(0).value), 100, 100)));
                }else
                    markerCar.icon(bitmapAuto);
                markerCar.data(car);
                //markerCar.anchor(0.5f, 1.0f);

                poiMarkersToAdd.add(markerCar);

            //}
        }


        if(!mPresenter.isFeeds || showCarsWithFeeds || isBookingCar || isTripStart)
            showPoiMarkers();


        //Stop sull'animazione del pulsante di refresh
        anim.cancel();
        if(mPresenter.isFeeds){
            ad.animationRefresh = false;
            circularLayout.init();
        }
    }

    //Mostri i pin sulla mappa
    private void showPoiMarkers(){

        if(poiMarkers == null)
            poiMarkers = new ArrayList<>();

       boolean bookedCarFind = false;

        //Verifico se ci sono marker da rimuovere dalla mappa perché non più presenti rispetto alla zona visualizzata
        List<Marker> copiaMarkers = new ArrayList<>(poiMarkers);
        for(com.androidmapsextensions.Marker markerOnMap : copiaMarkers){

            boolean find = false;
            for(MarkerOptions markerCar : poiMarkersToAdd){
                try {
                    if (((Car) markerCar.getData()).id.equals(((Car) markerOnMap.getData()).id))
                        find = true;
                }catch (IllegalStateException e){}
            }

            if(carbookingMarker != null){
                if(carbookingMarker == markerOnMap){
                    bookedCarFind = true;
                    find = true;
                }
            }

            //se non l'ho trovato, lo devo eliminare dalla mappa
            if(!find){
                poiMarkers.remove(markerOnMap);
                markerOnMap.remove();
            }else{
                setClusteringZoom(markerOnMap);
            }
        }

        //Verifico se i marker che devo disegnare sono già presenti nella mappa. Se non sono presenti, li devo aggiungere
        for(MarkerOptions markerCar : poiMarkersToAdd){

            boolean find = false;
            for(com.androidmapsextensions.Marker markerOnMap : poiMarkers){
                if(((Car) markerCar.getData()).id.equals(((Car) markerOnMap.getData()).id)) find = true;
            }

            if(!find) {
                com.androidmapsextensions.Marker myMarker = mMap.addMarker(markerCar);

                //Clusterizzo o meno in base al livello di zoom
                setClusteringZoom(myMarker);

                poiMarkers.add(myMarker);

                Car car = (Car) markerCar.getData();

                //Verifico se è attiva una prenotazione e se la targa dell'overley corrisponde a quella della macchina prenotata
                //if(isBookingCar || isTripStart){
                if(isBookingCar || (isTripStart && isTripParked)){
                    if(car.id.equals(carBooked.id)) {
                        carbookingMarker = myMarker;
                        bookedCarFind = true;
                    }
                }
            }

        }


        //Se è attiva una prenotazione, ma la macchina non è presente tra i risultati restituiti dal server aggiungo la macchina alla lista
        if(isBookingCar && !bookedCarFind){
            //Creo il marker
            MarkerOptions markerCar = new MarkerOptions().position(new LatLng(carBooked.latitude, carBooked.longitude));
            markerCar.icon(bitmapAuto);
            markerCar.data(carBooked);
            poiMarkersToAdd.add(markerCar);

            com.androidmapsextensions.Marker myMarker = mMap.addMarker(markerCar);
            poiMarkers.add(myMarker);

            carbookingMarker = myMarker;
        }

        setAnimatedMarker();
        setMarkerAnimation();

    }

    private void setClusteringZoom(com.androidmapsextensions.Marker mMarker){
        if(mMap.getCameraPosition().zoom >=12.5){
            mMarker.setClusterGroup(ClusterGroup.NOT_CLUSTERED);
        }else{
            mMarker.setClusterGroup(101);
        }
    }

    private void setAnimatedMarker(){

        //Ciclo i marker disegnati per trovare l'auto vicina
        if(poiMarkers != null && poiMarkers.size() > 0){

            for(com.androidmapsextensions.Marker markerNext : poiMarkers){
                Car markerNextData = ((Car) markerNext.getData());
                boolean freeCar = false;
                if(markerNextData.bonus != null&& !markerNextData.bonus.isEmpty() && markerNextData.bonus.get(0).status && markerNextData.bonus.get(0).type.equals("nouse")) freeCar = true;
                if(markerNextData.id.equals(carnext_id) && !freeCar){
                    carnextMarker = markerNext;
                }
            }

            setMarkerAnimation();
        }else{
            carnextMarker = null;
        }

    }

    //Metodo per nascondere i pin sulla mappa (richiamato in genere dal pulsante del menu radiale)
    private void hidePoiMarkers(){

        removeMarkers(poiMarkers);

        //setMarkerAnimation();
    }

    //Metodo richiamato quando si preme sul pin di un macchina
    private void onTapMarker(Car car){


        //Verifico se è attiva una prenotazione
        /*if(isBookingCar || isTripStart){

            //Se è la stessa macchina prenotata / in corsa faccio solo lo zoom
            if(carBooked.id.equals(car.id)){
                moveMapCameraToPoitWithZoom((double) carBooked.latitude, (double) carBooked.longitude, 17);
            }else {

                showPopupCar(car);
                moveMapCameraToPoitWithZoom((double) car.latitude, (double) car.longitude, 19);
            }
        }else {

            showPopupCar(car);
            moveMapCameraToPoitWithZoom((double) car.latitude, (double) car.longitude, 19);
        }*/

        if(car != null) {

            if (userMarker != null && userMarker.getData() != null && isTripStart && userLocation != null && ((Car) userMarker.getData()).id.equals(car.id)) {
                moveMapCameraToPoitWithZoom((double) userLocation.getLatitude(), (double) userLocation.getLongitude(), 19);
                showPopupCar(car);
            }else {
                if(userLocation != null && userMarker != null && userMarker.getData() != null){
                    if(!((Car) userMarker.getData()).id.equals(car.id)) {
                        moveMapCameraToPoitWithZoom((double) car.latitude, (double) car.longitude, 19);
                        showPopupCar(car);
                    }

                }else {
                    moveMapCameraToPoitWithZoom((double) car.latitude, (double) car.longitude, 19);
                    showPopupCar(car);
                }
            }
        }
    }

    //Metodo richiamato se il server non restituisce macchina da mostrare: stoppo l'animazione del pulsante "refresh"
    private void noCarsFountToDraw(){
        //Stop sull'animazione del pulsante di refresh
        anim.cancel();
        if(mPresenter.isFeeds){
            ad.animationRefresh = false;
            circularLayout.init();
        }
    }

    //Metodo utilizzato per trovare la macchina più vicina alla posizione dell'utente
    private void findNextCar(List<Car> carsList){

        String car_id = "";
        float distance = 10000000000000000000000.0f;


        if(userLocation != null && carsList != null) {

            for (Car car : carsList) {

                if(car != null) {
                    float dist = getDistance(car);

                    if (dist < distance) {
                        distance = dist;
                        car_id = car.id;
                        carNext = car;
                    }
                }
            }

        }

        carnext_id = car_id;

        if(!isBookingCar && !isTripStart && carPreSelected == null && carSelected == null) {
            if (carWalkingNavigation == null || (carWalkingNavigation != null && !carWalkingNavigation.id.equals(carNext.id))) {
                carWalkingNavigation = carNext;
                getWalkingNavigation();
            }
        }
    }

    private void setMarkerAnimation(){

        if(carnextMarker != null || carbookingMarker != null || isTripStart) {
            if (timer != null) timer.cancel();

            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (getActivity() != null) {

                                    List<BitmapDescriptor> drawableAnimArray = null;

                                    //Verifico se una prenotazione è attiva: il colore dell'animazione è giallo se c'è una prenotazione, altrimenti verde
                                    if (isBookingCar || isTripStart)
                                        drawableAnimArray = drawableAnimYellowArray;
                                    else drawableAnimArray = drawableAnimGreenArray;

                                    //Ogni x millisecondi cambio il frame
                                    if (isBookingCar || isTripStart) {

                                        if (carbookingMarker != null && isBookingCar && !isTripStart) {
                                            try {
                                                carbookingMarker.setIcon(drawableAnimArray.get(currentDrawable));
                                                carbookingMarker.setAnchor(0.5f, 0.70f);
                                            } catch (NullPointerException e) {
                                                carbookingMarker = null;
                                            }
                                        }

                                        if (isTripStart && !isTripParked) {
                                            try {
                                                if(userMarker != null) userMarker.setIcon(drawableAnimArray.get(currentDrawable));
                                                userMarker.setAnchor(0.5f, 0.65f);

                                                if (carbookingMarker != null) {
                                                    carbookingMarker.remove();
                                                    carbookingMarker = null;
                                                }

                                            } catch (NullPointerException e) {
                                            }
                                        } else if (isTripStart && isTripParked) {
                                            try {
                                                if(userMarker != null) userMarker.setIcon(bitmapUser);
                                                carbookingMarker.setIcon(drawableAnimArray.get(currentDrawable));
                                                carbookingMarker.setAnchor(0.5f, 0.65f);

                                            } catch (NullPointerException e) {
                                            }
                                        }

                                        if (carnextMarker != null)
                                            try {
                                                carnextMarker.setIcon(bitmapAuto);
                                                //carnextMarker.setAnchor(0.0f, 0.0f);
                                                carnextMarker = null; //ADD
                                            } catch (NullPointerException e) {
                                                carnextMarker = null;
                                            }
                                    } else {

                                        drawUserMarker();

                                        if (carbookingMarker != null)
                                            try {
                                                carbookingMarker.setIcon(bitmapAuto);
                                                //carbookingMarker.setAnchor(0.0f, 0.0f);
                                                carbookingMarker = null; //ADD
                                            } catch (NullPointerException e) {
                                                carbookingMarker = null;
                                            }
                                        if (carnextMarker != null) {
                                            try {
                                                carnextMarker.setIcon(drawableAnimArray.get(currentDrawable));
                                                carnextMarker.setAnchor(0.5f, 0.65f);
                                            } catch (NullPointerException e) {

                                                try {
                                                    for (Marker markerOnMap : poiMarkers) {
                                                        if (((Car) carnextMarker.getData()).id.equals(((Car) markerOnMap.getData()).id)) {
                                                            poiMarkers.remove(markerOnMap);
                                                        }
                                                    }

                                                    MarkerOptions markerCar = new MarkerOptions().position(new LatLng(((Car) carnextMarker.getData()).latitude, ((Car) carnextMarker.getData()).longitude));
                                                    markerCar.icon(getBitmapDescriptor(R.drawable.ic_auto));

                                                    com.androidmapsextensions.Marker myMarker = mMap.addMarker(markerCar);
                                                    myMarker.setData(carnextMarker.getData());
                                                    poiMarkers.add(myMarker);

                                                    carnextMarker = myMarker;

                                                }catch (ConcurrentModificationException c){}

                                            }

                                        }
                                    }

                                    if (drawableAnimArray != null && currentDrawable < drawableAnimArray.size() - 1)
                                        currentDrawable++;
                                    else currentDrawable = 0;
                                }

                            }
                        });
                    }
                }
            }, 100, 30);
        }
    }

    private void showCarOnMapClick(){
        //Non posso nascondere le macchine se c'è attiva prenotazione / corsa
        if(isBookingCar || isTripStart){

            //maps_hidecars_alert EEA

            final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                    getString(R.string.maps_hidecars_alert),
                    getString(R.string.ok),
                    null);
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                }
            });

        }
        else {
            if (showCarsWithFeeds) {
                showCarsWithFeeds = false;
                carFeedMapButton.setAlpha(.4f);
                hidePoiMarkers();
            } else {
                showCarsWithFeeds = true;
                carFeedMapButton.setAlpha(1.0f);

                if(getMapRadius() < 35000)
                    showPoiMarkers();
            }
        }
    }

    //Metodo per disegnare i poligoni sulla mappa
    private void drawPolygon(List<KmlServerPolygon> polygonList){

        removePolygons(polygonsMaps);

        if(polygonsMaps == null) polygonsMaps = new ArrayList<>();

        //Ciclo i poligoni
        for (KmlServerPolygon polygonss : polygonList){

            //Ciclo le coordinate di ogni poligono
            PolygonOptions polygonOptions = new PolygonOptions().strokeColor(Color.parseColor("#44ad4f")).fillColor(Color.parseColor("#1A44ad4f")).strokeWidth(4);
            for(LatLng coords : polygonss.coordinates){
                polygonOptions.add(coords);
            }

            com.androidmapsextensions.Polygon polygonGoogle = mMap.addPolygon(polygonOptions);
            polygonsMaps.add(polygonGoogle);

        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Utils
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Metodo per calcolare la distanza tra una macchina e la posizione dell'utente
    private float getDistance(Car car){
        Location locationA = new Location("point A");

        locationA.setLatitude(userLocation.getLatitude());
        locationA.setLongitude(userLocation.getLongitude());

        Location locationB = new Location("point B");

        locationB.setLatitude(car.latitude);
        locationB.setLongitude(car.longitude);

        return locationA.distanceTo(locationB);
    }

    private BitmapDescriptor getBitmapDescriptor(int icon){
        return BitmapDescriptorFactory.fromBitmap(getIconMarker(icon).getBitmap());
    }

    private BitmapDescriptor getBitmapDescriptor(Drawable drawable){
        return BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable));
    }

    private BitmapDescriptor getBitmapDescriptor(Bitmap bitmap){
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    //Metodo che permette di modificare la dimensione dell'immagine del pin presente sulla mappa
    private Bitmap resizeMapIcons(String iconName,int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()), options);  //BitmapFactory.decodeResource(a.getResources(), path, options);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    //Metodo che server per prelevare l'asset grafico corretto
    private BitmapDrawable getIconMarker(int icon){
        BitmapDrawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = (BitmapDrawable) getActivity().getDrawable(icon);
        }else{
            drawable = (BitmapDrawable) getResources().getDrawable(icon);
        }

        return drawable;
    }

    //Metodo per inserire un'icona sovrapposta al marker base (cerchio giallo con bordo verde) nel caso del marker delle città
    private Drawable makeBasicMarker(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 65, 65, false);
        Drawable[] layers = new Drawable[2];
        layers[0] = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_cluster));

        layers[1] = new BitmapDrawable(getResources(), tintImage(resizedBitmap));
        LayerDrawable ld = new LayerDrawable(layers);
        ld.setLayerInset(1, 10, 10, 10, 10); // xx would be the values needed so bitmap ends in the upper part of the image
        return ld;
    }

    //Metodo per inserire un'icona sovrapposta al marker base nel caso del marker dell'auto con minuti free
    /*private Drawable makeFreeMarker() {
        //Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 65, 65, false);
        Drawable[] layers = new Drawable[1];
        layers[0] = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_auto_free));

        //layers[1] = new BitmapDrawable(getResources(), tintImage(resizedBitmap));

        LayerDrawable ld = new LayerDrawable(layers);
        ld.setLayerInset(0, 10, 10, 10, 10); // xx would be the values needed so bitmap ends in the upper part of the image
        return ld;
    }*/

    private Drawable makeFreeMarker(Drawable backgroundImage, String text,
                                      int width, int height) {

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        // Create a canvas, that will draw on to canvasBitmap.
        Canvas imageCanvas = new Canvas(canvasBitmap);

        // Set up the paint for use with our Canvas
        Paint imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        imagePaint.setTextAlign(Paint.Align.CENTER);
        imagePaint.setColor(ContextCompat.getColor(getActivity(), R.color.white));
        imagePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        imagePaint.setTextSize(8 * getResources().getDisplayMetrics().density);

        // Draw the image to our canvas
        backgroundImage.draw(imageCanvas);

        // Draw the text on top of our image
        imageCanvas.drawText(text, width / 2, 48, imagePaint);

        // Combine background and text to a LayerDrawable
        LayerDrawable layerDrawable = new LayerDrawable(
                new Drawable[]{backgroundImage, new BitmapDrawable(canvasBitmap)});
        return layerDrawable;
    }

    //Metodo per modificare il colore di una bitmap
    private  Bitmap tintImage(Bitmap bitmap) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.mediumseagreen), PorterDuff.Mode.SRC_ATOP));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }

    //Metodo per ricavere l'indirizzo date le coordinate
    private String getAddress(float latitude, float longitude){
        String address = "";

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);


            if(!addresses.isEmpty() && addresses.get(0) != null) {

                String street = addresses.get(0).getThoroughfare(); //Nome della via
                String number = addresses.get(0).getSubThoroughfare(); //Numero civico

                if(street != null) address = street;
                if(address.length() > 0 && number != null) address += ", ";
                if(number != null) address += number;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    private void loginAlert(){
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.general_login_alert),
                getString(R.string.login),
                getString(R.string.cancel));
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
                mPresenter.setCarSelected(carSelected);
                Navigator.launchLogin(MapGoogleFragment.this, Navigator.REQUEST_LOGIN_MAPS);
                getActivity().finish();
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Popup Car
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void showPopupCar(Car car){

        if(!isAdded()) return;

        carSelected = car;

        carPreSelected = null;

        //Aggiorno la Walk Navigation
        if(!isBookingCar && !isTripStart){
            carWalkingNavigation = car;
            getWalkingNavigation();
        }

        carFeedMapButton.setAlpha(1.0f);
        showCarsWithFeeds = true;

        // Animazione
        popupCarView.setVisibility(View.VISIBLE);
        popupCarView.animate().translationY(0);

        //Popolo i dati dell'interfaccia

        //Targa
        plateTextView.setText(car.id);

        //Autonomia
        loadCarAutonomy(car.autonomy);
        mPresenter.loadCarInfoPopup(car.id);

        //Indirizzo
        String address = getAddress(car.latitude, car.longitude);
        if(address.length() > 0)
            addressTextView.setText(address);

        //Distanza
        if(userLocation != null){
            float distance = getDistance(car);
            showCarDistance(distance);
        }else{
            distanceTextView.setText(getString(R.string.maps_enablegps_label));
        }

        //Time
        if(userLocation != null){
            int timeF = Math.round(getDistance(car)/100);
            showCarDuration(timeF);
        }else{
            timeView.setVisibility(View.GONE);
        }

        //Tipologia popup
        boolean isCarBonus = (car.bonus != null&& !car.bonus.isEmpty() && car.bonus.get(0).status && car.bonus.get(0).type.equals("nouse")) ? true : false;
        if(isCarBonus && car.id.equals(carnext_id)){
            closestcarTextView.setText(getString(R.string.maps_closestcar_label) + "\n" + String.format(getString(R.string.maps_freecar_label), ""+car.bonus.get(0).value));
            closestcarView.setVisibility(View.VISIBLE);
        }
        else if(isCarBonus){
            closestcarTextView.setText(String.format(getString(R.string.maps_freecar_label), ""+car.bonus.get(0).value));
            closestcarView.setVisibility(View.VISIBLE);
        }
        else if(car.id.equals(carnext_id)){
            closestcarTextView.setText(getString(R.string.maps_closestcar_label));
            closestcarView.setVisibility(View.VISIBLE);
        }else{
            closestcarView.setVisibility(View.GONE);
        }
    }

    private void showCarDistance(float distance){
        String distanceString = "";

        if(distance < 1000){
            distance = Math.round(distance);
            distanceString = String.format(getString(R.string.maps_distance_label), (int) distance);
        }
        else{
            distanceString = String.format(getString(R.string.maps_distancekm_label), distance/1000);
        }

        distanceTextView.setText(distanceString);
    }

    private void showCarDuration(int timeF){
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
    }

    private void loadCarInfo(Car car){
        loadCarAutonomy(car.autonomy);
    }

    private void loadCarAutonomy(int autonomy){
        if(autonomy < 50) autonomy = autonomy - 10;
        autonomyTextView.setText(Html.fromHtml(String.format(getString(R.string.maps_autonomy_label), (int) autonomy)));
        if(autonomy == 0){
            autonomyTextView.setVisibility(View.GONE);
        }else{
            autonomyTextView.setVisibility(View.VISIBLE);
        }

    }

    //Metodo per chiudere il popup che mostra le informazioni della macchina
    private void closePopup(){
        mPresenter.setCarSelected(null);
        popupCarView.animate().translationY(popupCarView.getHeight());

        carSelected = null;

        if(!isBookingCar && !isTripStart) removeWalkingNavigation();
    }

    //Metodo per verificare se è possibile aprire le portiere (utente autenticato)
    private void checkOpenDoor(){
        if(mPresenter.isAuth()){
            openDoors();
        }else{
            loginAlert();
        }
    }

    //Metodo per aprire le portiere
    private void openDoors(){

        boolean openDoorOk = false;

        //Log.w("carSelected",": "+carSelected.id);
        //Log.w("carBooked",": "+carBooked.id);



        if(openDoorFromBooking) openDoorOk = true;
        else if(isBookingCar || isTripStart){

            if(carSelected != null && carBooked != null){

                if(carSelected.id.equals(carBooked.id) && (isBookingCar || (isTripStart && isTripParked))){
                    openDoorOk = true;
                }
            }else if(carBooked != null){
                openDoorOk = true;
            }
        }else openDoorOk = true;

        if(openDoorOk){

            Car carToOpen = carSelected;
            if(isBookingCar || isTripStart) carToOpen = carBooked;

            if(carToOpen != null){
                if(userLocation != null){
                    //Calcolo la distanza
                    if(getDistance(carToOpen) <= 500 || true){ //TODO: reintegrare il controllo

                        //Procediamo con le schermate successive
                        onClosePopup();
                        if(isTripStart && isTripParked) {
                            unparkAction = true;
                            mPresenter.openDoor(carToOpen, "unpark");
                        }else {
                            unparkAction = false;
                            mPresenter.openDoor(carToOpen, "open");
                        }

                    }else{
                        CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                                getString(R.string.maps_opendoordistance_alert),
                                getString(R.string.ok),
                                null);
                        cdd.show();
                    }
                }else{
                    //Informo l'utente che la localizzazione non è attiva
                    /*final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
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
                    });*/
                    //Procediamo con le schermate successive
                    onClosePopup();
                    if(isTripStart && isTripParked) {
                        unparkAction = true;
                        mPresenter.openDoor(carToOpen, "unpark");
                    }else {
                        unparkAction = false;
                        mPresenter.openDoor(carToOpen, "open");
                    }

                }
            }

        }else{
            //Mostro un'alert di avviso
            final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                    getString((isTripStart) ? R.string.booking_tripcar_alert : R.string.booking_bookedcar_alert),
                    getString(R.string.ok),
                    null);
            cdd.show();
            cdd.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdd.dismissAlert();
                }
            });
        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Walking Navigation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void getWalkingNavigation(){

        if(userLocation != null && carWalkingNavigation != null && getDistance(carWalkingNavigation) <= 10000) {
            walkingDestination.setLatitude(carWalkingNavigation.latitude);
            walkingDestination.setLongitude(carWalkingNavigation.longitude);

            if(getActivity() != null) mPresenter.getRoutes(getActivity(), userLocation, walkingDestination, "walking");
        }
    }

    //Aggiorno la Walk Navigation
    private void updateWalkingNavigation(ResponseGoogleRoutes googleRoutes){

        if(carWalkingNavigation != null) {
            if (polyWalking != null) polyWalking.remove();

            if (googleRoutes.routes != null && googleRoutes.routes.size() > 0) {
                List<LatLng> m_path = PolyUtil.decode(googleRoutes.routes.get(0).overview_polyline.points);
                polyWalkingOptions = new PolylineOptions().addAll(m_path);
                polyWalkingOptions.color(Color.parseColor("#336633"));
                polyWalkingOptions.jointType(JointType.ROUND);
                List<PatternItem> pattern = Arrays.<PatternItem>asList(
                        new Dot(), new Gap(15)); //new Dot(), new Gap(20), new Dash(30), new Gap(20)
                polyWalkingOptions.pattern(pattern);
                polyWalkingOptions.width(20);
                polyWalking = mMap.addPolyline(polyWalkingOptions);
            }
        }

        //Aggiorno distanza popover
        try{
            distanceView.setVisibility(View.VISIBLE);

            float distance = googleRoutes.routes.get(0).legs.get(0).distance.value;
            showCarDistance(distance);

        }catch (NullPointerException e){}

        //Aggiorno la durata popover
        try{
            timeView.setVisibility(View.VISIBLE);

            int duration = googleRoutes.routes.get(0).legs.get(0).duration.value;
            showCarDuration(duration/60);
        }catch (NullPointerException e){}

    }

    //Elimino il Walk Navigation
    private void removeWalkingNavigation(){
        carWalkingNavigation = null;

        if(!isTripStart && !isBookingCar && carNext != null){

            if(carSelected == null) carWalkingNavigation = carNext;
            getWalkingNavigation();
        }
        else{
            if(polyWalking != null) polyWalking.remove();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Booking
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Metodo per verificare se è possibile prenotare la macchina (utente autenticato)
    private void checkBookingCar(){
        if(mPresenter.isAuth())

            if(isBookingCar || isTripStart){
                //Mostro un'alert di avviso
                final CustomDialogClass cdd = new CustomDialogClass(getActivity(),
                        getString((isTripStart) ? R.string.booking_tripcar_alert : R.string.booking_bookedcar_alert),
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
                bookingCar();
        else{
            loginAlert();
        }
    }

    //Metodo richiamato per prenotare una macchina selezionata
    private void bookingCar(){
        float user_lat = 0;
        float user_lon = 0;
        if(userLocation != null){
            user_lat = (float) userLocation.getLatitude();
            user_lon = (float) userLocation.getLongitude();
        }
        mPresenter.bookingCar(carSelected, user_lat, user_lon, getContext());
    }

    //Metodo per mostrare le informazioni sulla prenotazione
    private void reservationInfo(Car mCar, Reservation mReservation){

        if(!isBookingCar){
            //moveMapCameraToPoitWithZoom((double) carSelected.latitude + 0.0002, (double) carSelected.longitude, 19);
            if(userLocation != null)
                moveMapCameraToPoitWithZoom(userLocation.getLatitude() + 0.0002, userLocation.getLongitude(), 19);
        }

        isBookingCar = true;
        ad.carAlpha = false;
        circularLayout.init();

        carBooked = mCar;
        reservation = mReservation;

        //removeMarkers(poiMarkers);
        if(poiMarkersToAdd == null) poiMarkersToAdd = new ArrayList<>();
        if(poiMarkers == null) poiMarkers = new ArrayList<>();


        setMarkerAnimation();

        onClosePopup();
        openViewBookingCar();
    }


    //Visualizzio le informazioni della prenotazione
    private void openViewBookingCar(){

        int pinUser = mPresenter.getUser().userInfo.pin;
        String plateBooking = carBooked.id;
        String addressBooking = getAddress(carBooked.latitude, carBooked.longitude);
        String timingBookin = "";


        if(reservation != null){

            long unixTime = System.currentTimeMillis() / 1000L;
            int diffTime = (int) (unixTime - reservation.timestamp_start);

            if(countDownTimer != null) countDownTimer.cancel();
            countDownTimer = new CountDownTimer((reservation.length - diffTime) * 1000, 1000) {

                public void onTick(long millisUntilFinished) {

                    int mn = (int) (millisUntilFinished / 1000 / 60);
                    int sec = (int) (millisUntilFinished / 1000 % 60);
                    String mnStr = (mn<10 ? "0" : "")+mn;
                    String secStr = (sec<10 ? "0" : "")+sec;

                    if(mnStr.equals("00") && secStr.equals("01")){
                        removeReservationInfo();
                        countDownTimer.cancel();
                        return;
                    }

                    if(getActivity() != null)
                        expiringTimeTextView.setText(Html.fromHtml(String.format(getString(R.string.booking_expirationtime), mnStr+":"+secStr)));
                    else if(countDownTimer != null) countDownTimer.cancel();
                }

                public void onFinish() {}
            }.start();
        }
        if(isTripStart){

            if (timerTripDuration != null) timerTripDuration.cancel();

            timerTripDuration = new Timer();


            timerTripDuration.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                long unixTime = System.currentTimeMillis() / 1000L;
                                int diffTime = (int) (unixTime - tripTimestampStart) * 1000;

                                int hh = (int) (diffTime / 1000 / 60 / 60);
                                int mn = (int) (diffTime / 1000 / 60 % 60);
                                int sec = (int) (diffTime / 1000 % 60);
                                String hhStr = (hh < 10 ? "0" : "") + hh;
                                String mnStr = (mn < 10 ? "0" : "") + mn;
                                String secStr = (sec < 10 ? "0" : "") + sec;


                                if (getActivity() != null)
                                    tripDurationTextView.setText(Html.fromHtml(String.format(getString(R.string.booking_durationtrip_label), hhStr + ":" + mnStr + ":" + secStr)));
                                else if (countDownTimer != null) countDownTimer.cancel();

                            }
                        });
                    }
                }
            }, 1000, 1000);
        }
        //----

        //Popolo le informazioni
        userPinTextView.setText(""+pinUser);//pinUser
        bookingPlateTextView.setText(String.format(getString(R.string.booking_plate_label), plateBooking));


        if(isTripStart){
            if(!carBooked.parking) bookingTitleTextView.setText(getString(R.string.booking_tripactive_label));
            else bookingTitleTextView.setText(getString(R.string.tripend_parkerdcar_label));

            bookingAddressTextView.setVisibility(View.GONE); //getString(R.string.booking_durationtrip_label)
            timeIconImageView.setImageDrawable(getIconMarker(R.drawable.ic_time_2));
        }else{
            bookingTitleTextView.setText(getString(R.string.booking_active_label));
            bookingAddressTextView.setText(addressBooking);
            bookingAddressTextView.setVisibility(View.VISIBLE);
            expiringTimeTextView.setText(Html.fromHtml(String.format(getString(R.string.booking_expirationtime), timingBookin)));
            timeIconImageView.setImageDrawable(getIconMarker(R.drawable.ic_time));
        }

        //Apro le informazioni
        bookingCarView.setVisibility(View.VISIBLE);


        LinearLayout.LayoutParams parameter =  (LinearLayout.LayoutParams) openDoorBookingButton.getLayoutParams();
        parameter.setMargins((int) (10 * getResources().getDisplayMetrics().density), 0, (int) (5 * getResources().getDisplayMetrics().density), 0); // left, top, right, bottom
        openDoorBookingButton.setLayoutParams(parameter);


        //Verifico se la corsa è iniziata: nel caso nascondo le informazioni non necessarie
        if(isTripStart){
            expiringTimeTextView.setVisibility(View.GONE);
            tripDurationTextView.setVisibility(View.VISIBLE);

            if(!carBooked.parking){ //Auto in corsa

                openButtonBookingView.setVisibility(View.GONE);

            }else{ //Auto parcheggiata

                parameter.setMargins((int) (40 * getResources().getDisplayMetrics().density), 0, (int) (40 * getResources().getDisplayMetrics().density), 0);
                openDoorBookingButton.setLayoutParams(parameter);

                openButtonBookingView.setVisibility(View.VISIBLE);
                deleteBookingButton.setVisibility(View.GONE);
            }
        }else{ //Prenotazione
            openButtonBookingView.setVisibility(View.VISIBLE);
            deleteBookingButton.setVisibility(View.VISIBLE);
            expiringTimeTextView.setVisibility(View.VISIBLE);
            tripDurationTextView.setVisibility(View.GONE);
        }



        for(com.androidmapsextensions.Marker marker : poiMarkers){
            if(((Car) marker.getData()).id.equals(plateBooking)){
                carbookingMarker = marker;
            }
        }
        if(carbookingMarker == null && !isTripStart){
            //Aggiungo la macchina
            MarkerOptions markerCar = new MarkerOptions().position(new LatLng(carBooked.latitude, carBooked.longitude));
            //markerCar.icon(getBitmapDescriptor(R.drawable.autopulse0001));
            markerCar.icon(bitmapAuto);
            markerCar.data(carBooked);
            poiMarkersToAdd.add(markerCar);

            carbookingMarker = mMap.addMarker(markerCar);
            poiMarkers.add(carbookingMarker);
        }

        carFeedMapButton.setAlpha(1.0f);


        //Aggiorno la Walk Navigation
        if(isTripStart){
            if(!carBooked.parking) { //Auto in corsa
                if (polyWalking != null) polyWalking.remove();
                hideLoading();
            }else{ //Auto parcheggiata
                carWalkingNavigation = carBooked;

                getWalkingNavigation();
            }
        }else {
            carWalkingNavigation = carBooked;

            getWalkingNavigation();
        }
    }

    //Metodo chiamato quando l'utente decide di annullare la prenotazione
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

    //Nasconde le informazioni della prenotazione
    private void closeViewBookingCar(){

        if(countDownTimer != null) countDownTimer.cancel();

        //Nascondo le informazioni della prenotazione cancellata
        if(bookingCarView != null)
            bookingCarView.setVisibility(View.GONE);


        //Tolgo l'animazione al pin
        setAnimatedMarker();
        //setMarkerAnimation();

        hideLoading();
    }

    //Metodo quando arriva dal server la conferma che la prenotaizone è stata annullata
    private void confirmDeletedCar(){

        mPresenter.loadPlates();

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

                //Aggiorno la Walking Animation
                removeWalkingNavigation();
            }
        });
    }

    //Metodo per nascondere le informazioni delle prenotazione
    private void hideReservationInfo(){

        isBookingCar = false;
        carBooked = null;
        if(countDownTimer != null) countDownTimer.cancel();
        closeViewBookingCar();
    }

    private void showBookingCarInfo(Reservation mReservation){
        reservation = mReservation;

        onClosePopup();
        isBookingCar = true;
        ad.carAlpha = false;
        circularLayout.init();
        openViewBookingCar();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Trip
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Metodo richiamato quando c'è una corsa attiva e occorre mostrare la grafica
    private void tripInfo(final Car car, int timestamp_start){

        if(unparkAction){
            car.parking = false;
            unparkAction = false;
        }

        //TODO Remove
        /*car.parking = test_corsa;
        if(test_corsa) test_corsa = false;
        else test_corsa = true;*/
        //---



        isTripStart = true;
        isBookingCar = false;
        isTripParked = car.parking;
        tripTimestampStart = timestamp_start;
        carBooked = car;

        if(!isTripStart) {
            if (isTripParked)
                moveMapCameraToPoitWithZoom(carBooked.latitude + 0.0002, (double) carBooked.longitude, 19);
            else
            if(userLocation != null) moveMapCameraToPoitWithZoom(userLocation.getLatitude() + 0.0002, userLocation.getLongitude(), 19);
        }


        //removeMarkers(poiMarkers);
        if(poiMarkersToAdd == null) poiMarkersToAdd = new ArrayList<>();
        if(poiMarkers == null) poiMarkers = new ArrayList<>();


        //Aggiungo la macchina
        if(isTripParked) {

            if(userMarker != null) userMarker.setData(null);

            if(carbookingMarker == null) {
                boolean find = false;
                for(Marker markerOnMap : poiMarkers){
                    if (car.id.equals(((Car) markerOnMap.getData()).id)){
                        find = true;
                        carbookingMarker = markerOnMap;
                    }
                }

                if(find){
                    carbookingMarker.remove();
                }

                MarkerOptions markerCar = new MarkerOptions().position(new LatLng(car.latitude, car.longitude));
                markerCar.icon(getBitmapDescriptor(R.drawable.ic_auto));
                markerCar.data(car);
                poiMarkersToAdd.add(markerCar);

                com.androidmapsextensions.Marker myMarker = mMap.addMarker(markerCar);
                myMarker.setData(car);
                poiMarkers.add(myMarker);

                carbookingMarker = myMarker;
            }else{
                carbookingMarker.setIcon(bitmapAuto);
                carbookingMarker.setPosition(new LatLng(car.latitude, car.longitude));
            }
        }else{
            if(userMarker != null) userMarker.setData(car);
        }

        setMarkerAnimation();

        openViewBookingCar();
    }

    //Metodo richiamato per togliere tutte le informazioni della corsa attiva
    private void hideTripInfo(){
        isTripStart = false;
        isBookingCar = false;
//        carSelected = null;

        removeWalkingNavigation();
        closeViewBookingCar();
    }

    private void showNotification(int start, int end){

        int diffTime = (int) (end - start);

        co2 = ((float) diffTime)/60/60*17*106;  //((minuti÷60)×17)×106


        float timeRide = diffTime/60.0f;
        double dec = timeRide - Math.floor(timeRide);

        diffTime = diffTime/60;
        if(dec > 0.5) diffTime++;


        ((MapGoogleActivity) getActivity()).showNotification(String.format(getString(R.string.tripend_notification_label), diffTime), mNotificationListener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Feeds
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void showFeedsOnMap(List<Feed> feedsList){

        if(feedsMarker != null)
            removeMarkers(feedsMarker);

        feedsMarker = new ArrayList<>();

        for(final Feed feed : feedsList){

            //Creo il marker
            com.androidmapsextensions.Marker markerFeed = mMap.addMarker(new MarkerOptions().position(new LatLng(feed.informations.address.latitude, feed.informations.address.longitudef)));
            markerFeed.setIcon(getBitmapDescriptor(R.drawable.ic_cluster_transparent));
            markerFeed.setClusterGroup(202);
            markerFeed.setData(feed);

            feedsMarker.add(markerFeed);



            //Disegno i componenti grafici
            final com.androidmapsextensions.Marker finalMarkerFeed = markerFeed;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    try {
                        finalMarkerFeed.setIcon(getBitmapDescriptor(bitmap));
                    }catch (NullPointerException e){}
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };

            int markerWidth = (int) (38 * getResources().getDisplayMetrics().density);
            int markerHeight = (int) (46 * getResources().getDisplayMetrics().density);

            String feedMarkerIconUri = feed.category.media.images.marker.uri;

            if(feed.informations.sponsored.equals("true")){
                markerWidth = (int) (46 * getResources().getDisplayMetrics().density);
                markerHeight = (int) (55 * getResources().getDisplayMetrics().density);

                feedMarkerIconUri = feed.media.images.icon.uri;
            }

            Picasso.with(getActivity()).load(feedMarkerIconUri).resize(markerWidth, markerHeight).into(target);

            markerFeed.setTag(target);

        }

        //Stop sull'animazione del pulsante di refresh
        anim.cancel();
        if(mPresenter.isFeeds){
            ad.animationRefresh = false;
            circularLayout.init();
        }
    }

    private void closeFeedPopup(){
        feedSelected = null;
        popupFeedView.animate().translationY(popupFeedView.getHeight());
    }

    private void feedBookingClick(){
        if(carNext != null && userLocation != null) {

            if(!showCarsWithFeeds)
                showPoiMarkers();

            showCarsWithFeeds = true;

            onCloseFeedPopup();

            showPopupCar(carNext);

            moveMapCameraToPoitWithZoom((double) carNext.latitude, (double) carNext.longitude, 19);

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

    private void onTapFeedMarker(Feed feed){

        feedSelected = feed;

        int feedColor = Color.parseColor(feed.appearance.color.rgb);

        if(!feed.informations.sponsored.equals("true")) feedColor = Color.parseColor(feed.appearance.color.rgb_default);

        //Zoom sulla mappa
        moveMapCameraToPoitWithZoom((double) feed.informations.address.latitude, (double) feed.informations.address.longitudef, 19);

        //Immagine copertina
        ImageUtils.loadImage(feedImageView, feed.media.images.image.uri);

        //Overlay copertina
        feedOverlayView.setBackgroundColor(feedColor);

        //Advantage
        feedTriangleImageView.setColorFilter(feedColor);
        if(feed.informations.advantage_top == null) {

            feedTriangleView.setVisibility(View.GONE);

        }else if(feed.informations.advantage_top.isEmpty()) {

            feedTriangleView.setVisibility(View.GONE);

        }else {
            feedTriangleView.setVisibility(View.VISIBLE);
            feedAdvantageTextView.setText(StringsUtils.fromHTML(feed.informations.advantage_top));
        }

        //Icona
        ImageUtils.loadImage(feedIconImageView, feed.category.media.images.icon.uri);
        GradientDrawable backgroundShape = (GradientDrawable) feedIconImageView.getBackground();
        backgroundShape.setColor(feedColor);

        //Data
        feedDateTextView.setText(StringsUtils.fromHTML(feed.informations.date.friendly));

        //Launch title
        if(feed.informations.launch_title.isEmpty()){
            feedLaunchTitleTextView.setVisibility(View.GONE);
        }else{
            feedLaunchTitleTextView.setVisibility(View.VISIBLE);
            feedLaunchTitleTextView.setText(StringsUtils.fromHTML(feed.informations.launch_title));
            feedLaunchTitleTextView.setTextColor(feedColor);
        }

        //Titolo
        feedTitleTextView.setText(StringsUtils.fromHTML(feed.title));

        //Location
        feedLocationTextView.setText(StringsUtils.fromHTML(feed.informations.location + ", " +  feed.informations.address.friendly +  ", " + feed.informations.city.name));

        //Advantage bottom
        if(feed.informations.advantage_bottom.isEmpty()){
            feedAdvantageBottomTextView.setVisibility(View.GONE);
        }else{
            feedAdvantageBottomTextView.setVisibility(View.VISIBLE);
            feedAdvantageBottomTextView.setText(StringsUtils.fromHTML(feed.informations.advantage_bottom));

            if(feed.informations.sponsored.equals("true"))
                feedAdvantageBottomTextView.setTextColor(feedColor);
            else
                feedAdvantageBottomTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.taupegray));
        }

        //Mi interessa
        setFeedInters();

        // Animazione
        popupFeedView.setVisibility(View.VISIBLE);
        popupFeedView.animate().translationY(0);
    }

    private void feedInters(){
        if(feedSelected != null) {
            SharedPreferences mPref = getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            Type fooType = new TypeToken<List<String>>() {
            }.getType();
            Gson gson = new Gson();
            List<String> feedsInterested = (ArrayList<String>) gson.fromJson(mPref.getString(getString(R.string.preference_feeds), "[]"), fooType);

            boolean isInters = false;
            for (String feed_id : feedsInterested) {
                if (feed_id.equals(feedSelected.id)) {
                    isInters = true;
                }
            }

            if (isInters) {
                feedIntersImageView.setVisibility(View.VISIBLE);
            } else {
                feedIntersImageView.setVisibility(View.GONE);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Quando l'utente preme il pulsante di chiusura del popup con il dettaglio di una macchina
    @OnClick(R.id.closePopupButton)
    public void onClosePopup() {
        closePopup();
    }

    //Quando l'utente preme il pulsante di chiusura del popup dei feeds
    @OnClick(R.id.closeFeedPopupButton)
    public void onCloseFeedPopup() {
        closeFeedPopup();
    }

    @OnClick(R.id.openDoorButton)
    public void onOpenDoor(){
        openDoorFromBooking = false;
        checkOpenDoor();
    }

    @OnClick(R.id.bookingCarButton)
    public void onBookingCar(){
        checkBookingCar();
    }

     //Metodo richiamato quando l'utente scrive nella casella di testo
    @OnTextChanged(value = R.id.searchEditText,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void searchEditText() {

        if(searchEditText.length() > 0)
            cancelButtonSearch.setVisibility(View.VISIBLE);
        else
            cancelButtonSearch.setVisibility(View.GONE);
        timerEditText.cancel();
        timerEditText = new Timer();
        timerEditText.schedule(
                new TimerTask() {
                    @Override
                    public void run() {

                        if(!searchItemSelected)
                            initMapSearch();
                        else searchItemSelected = false;
                    }
                },
                DELAY
        );
    }

    @OnClick(R.id.cancelButtonSearch)
    public void onCancelButtonSearch(){
        searchEditText.setText("");
    }

    @OnClick(R.id.microphoneImageView)
    public void onSearchMicrophone(){
        onClosePopup();
        startSpeechToText();
    }

    @OnClick(R.id.deleteBookingButton)
    public void onDeleteBookingButton(){
        deleteBooking();
    }

    @OnClick(R.id.refreshMapButtonView)
    public void onRefreshMap() {
        if(mMap != null)
            refreshMap();
    }

    @OnClick(R.id.centerMapButtonView)
    public void onCenterMap() {
        centerMap();
    }

    /*@OnClick(R.id.orientationMapButtonView)
    public void onOrientationMap() {
        if(mMap != null) orientationMap();
    }*/

    @OnClick(R.id.assistanceButtonView)
    public void launchAssistanceMap() {
        if(mMap != null) launchAssistance (MapGoogleFragment.this);
    }

    @OnClick(R.id.carFeedMapButtonView)
    public void onShowCarOnMapClick() {
        showCarOnMapClick();
    }

    @OnClick(R.id.feedDiscoverButton)
    public void onDiscoverClick(){
        Navigator.launchFeedsDetail(this, feedSelected);
    }


    @OnClick(R.id.feedBookingButton)
    public void onFeedBookingClick(){
        feedBookingClick();
    }

    @OnClick(R.id.openDoorBookingButton)
    public void openDoorBookingButton(){
        openDoorFromBooking = true;
        checkOpenDoor();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void showCars(List<Car> carsList) {
        if(getMapRadius() < 35000)
            showCarsOnMap(carsList);
    }

    @Override
    public void showFeeds(List<Feed> feedsList) {
        showFeedsOnMap(feedsList);
    }

    @Override
    public void noCarsFound() {
        noCarsFountToDraw();
    }

    @Override
    public void showSearchResult(List<SearchItem> searchItemList) {
        setSearchViewHeight();
        if(searchItemList.size() > 15)
            mAdapter.setData(searchItemList.subList(0, 15));
        else
            mAdapter.setData(searchItemList);
    }

    @Override
    public void showBookingCar(Reservation reservation) {
        showBookingCarInfo(reservation);
    }

    @Override
    public void showConfirmDeletedCar() {
        confirmDeletedCar();
    }

    @Override
    public void showTripInfo(Car car, int timestamp_start) {
        tripInfo(car, timestamp_start);
    }

    @Override
    public void setTripInfo(Trip trip) {
        //Non popolare - metodo da rimuovere
    }

    @Override
    public void removeTripInfo() {
        hideTripInfo();
    }

    @Override
    public void showReservationInfo(Car mCar, Reservation mReservation) {
        reservationInfo(mCar, mReservation);
    }

    @Override
    public void setReservationInfo(Car mCar, Reservation mReservation) {
        //Non popolare - metodo da rimuovere
    }

    @Override
    public void removeReservationInfo() {
        hideReservationInfo();
    }

    @Override
    public void openTripEnd(int timestamp) {
        Navigator.launchTripEnd(this, timestamp);
    }

    @Override
    public void openNotification(int start, int end) {
        showNotification(start, end);
        refreshCars();
    }

    @Override
    public void openReservationNotification() {
        removeWalkingNavigation();
        hideLoading();
        if(System.currentTimeMillis() > (reservation.timestamp_start + reservation.length) * 1000L )
            ((MapGoogleActivity) getActivity()).showNotification(getString(R.string.booking_timeend_label), null);
    }

    @Override
    public void openPreselectedCarPopup(Car car) {
        carPreSelected = car;
    }

    @Override
    public void showCity(List<City> cityList) {
        drawCityMarkerOnMap(cityList);
    }

    @Override
    public void setFeedInters() {
        feedInters();
    }

    @Override
    public void setNextCar(List<Car> carsList){
        findNextCar(carsList);
        setAnimatedMarker();
    }

    @Override
    public void showPolygon(List<KmlServerPolygon> polygonList){
        drawPolygon(polygonList);
    }

    @Override
    public void carAlreadyBooked(){
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.booking_alreadybookedcar_alert),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
            }
        });
    }

    @Override
    public void carBusyError() {
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.booking_car_busy_alert),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
            }
        });

    }

    @Override
    public void tooManyReservationError() {
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.booking_other_tripcar_alert),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
            }
        });

    }

    @Override
    public void reserveOnTripError() {
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.booking_other_tripcar_alert),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
            }
        });

    }

    @Override
    public void unauthorizedError() {
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.error_generalerror_alert),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
            }
        });

    }

    @Override
    public void generalError(){
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.error_generalerror_alert),
                getString(R.string.ok),
                null);
        cdd.show();
        cdd.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cdd.dismissAlert();
            }
        });
    }

    @Override
    public void openCarNotification() {
        final CustomDialogClass cdd=new CustomDialogClass(getActivity(),
                getString(R.string.tripstart_notification_label),
                2500);
        cdd.show();
    }

    @Override
    public void onLoadCarInfo(Car car){
        loadCarInfo(car);
    }

    @Override
    public void onUpdateWalkingNavigation(ResponseGoogleRoutes googleRoutes){
        if(!isTripStart || (isTripStart && isTripParked)) updateWalkingNavigation(googleRoutes);
        //hideLoading();
    }

    @Override
    public void showLoading(){
        ((BaseActivity) getActivity()).showLoadingChronology();
    }

    @Override
    public void hideLoading(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(getActivity() != null) ((BaseActivity) getActivity()).hideLoadingChronology();
            }
        }, 2000);
    }


    //Classe per customizzare il cluster
    public class DemoClusterOptionsProvider implements ClusterOptionsProvider {

        private final int[] res = {R.drawable.ic_cluster, R.drawable.ic_cluster, R.drawable.ic_cluster, R.drawable.ic_cluster, R.drawable.ic_cluster};
        private final int[] res_transparent = {R.drawable.ic_cluster_feed, R.drawable.ic_cluster_feed, R.drawable.ic_cluster_feed, R.drawable.ic_cluster_feed, R.drawable.ic_cluster_feed};

        private final int[] forCounts = {10, 100, 1000, 10000, Integer.MAX_VALUE};

        private Bitmap[] baseBitmaps;
        private Bitmap[] baseBitmapsFeeds;

        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Rect bounds = new Rect();

        private ClusterOptions clusterOptions = new ClusterOptions().anchor(0.5f, 0.5f);

        public DemoClusterOptionsProvider(Resources resources) {

            baseBitmaps = new Bitmap[res.length];
            for (int i = 0; i < res.length; i++) {
                baseBitmaps[i] = BitmapFactory.decodeResource(resources, res[i]);
            }
            baseBitmapsFeeds = new Bitmap[res_transparent.length];
            for (int i = 0; i < res_transparent.length; i++) {
                baseBitmapsFeeds[i] = BitmapFactory.decodeResource(resources, res_transparent[i]);
            }
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(15 * getResources().getDisplayMetrics().density);
        }

        @Override
        public ClusterOptions getClusterOptions(List<com.androidmapsextensions.Marker> markers) {

            int markersCount = markers.size();

            Bitmap base = null;
            int textColor = R.color.darkpastelgreen;
            int i = 0;
            do {
                if(markers.get(i).getData().getClass().equals(Car.class)) {
                    base = baseBitmaps[i];
                    textColor = R.color.darkpastelgreen;
                }else if(markers.get(i).getData().getClass().equals(Feed.class)){
                    base = baseBitmapsFeeds[i];
                    textColor = R.color.colorAccent;
                }


            } while (markersCount >= forCounts[i++]);

            Bitmap bitmap = base.copy(Bitmap.Config.ARGB_8888, true);

            //Verifico se all'interno dei gruppo di marker c'è quello relativo alla macchina più vicina
            boolean findNextCar = false;
            for(Marker markerOnCluster : markers){
                if(markerOnCluster.getData().getClass().equals(Car.class)){
                    if(((Car) markerOnCluster.getData()).id.equals(carnext_id)){
                        findNextCar = true;
                    }
                }
            }

            String text = String.valueOf(markersCount);
            paint.getTextBounds(text, 0, text.length(), bounds);
            paint.setColor(ContextCompat.getColor(getContext(), textColor));
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            float x = bitmap.getWidth() / 2.0f;
            float y = (bitmap.getHeight() - bounds.height()) / 2.0f - bounds.top;

            Canvas canvas = new Canvas(bitmap);
            canvas.drawText(text, x, y, paint);

            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
            /*if(findNextCar){
                clusterOptions.alpha(0.0f);
            }
            else{
                clusterOptions.alpha(1.0f);
            }*/

            return clusterOptions.icon(icon);
        }
    }
}
