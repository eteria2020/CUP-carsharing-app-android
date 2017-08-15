package it.sharengo.development.ui.mapgoogle;

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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
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
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.example.x.circlelayout.CircleLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import it.handroix.map.HdxFragmentMapHelper;
import it.sharengo.development.R;
import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.Feed;
import it.sharengo.development.data.models.Reservation;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.map.BaseMapFragment;
import it.sharengo.development.ui.components.CustomDialogClass;
import it.sharengo.development.ui.map.MapActivity;
import it.sharengo.development.ui.map.MapFragment;
import it.sharengo.development.ui.mapgoogle.CircleLayout.MyCircleLayoutAdapter;
import it.sharengo.development.utils.ImageUtils;

import static android.content.Context.MODE_PRIVATE;


public class MapGoogleFragment extends BaseMapFragment<MapGooglePresenter> implements MapGoogleMvpView, OnMapReadyCallback, LocationListener {

    private static final String TAG = MapGoogleFragment.class.getSimpleName();


    public static final String ARG_TYPE = "ARG_TYPE";
    private int type = 0;

    private View view;

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
    private Reservation reservation;
    private int tripTimestampStart;
    private float co2;
    private View.OnClickListener mNotificationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Navigator.launchTripEnd(MapGoogleFragment.this, co2);
        }
    };

    /* Map */
    private int mapRadius;
    private com.androidmapsextensions.Marker userMarker;
    private List<com.androidmapsextensions.Marker> poiMarkers;
    private List<MarkerOptions> poiMarkersToAdd;
    private List<com.androidmapsextensions.Marker> feedsMarker;
    private List<com.androidmapsextensions.Marker> poiCityMarkers;
    private ClusterManager<ClusterItem> mClusterManager;
    private String carnext_id;
    private Car carNext;
    private Car carSelected;
    private com.androidmapsextensions.Marker carnextMarker, carbookingMarker;
    private int currentDrawable = 0; //frame dell'animazione della macchiana più vicina
    //private int[] drawableAnimGreenArray = new int[]{R.drawable.autopulse0001, R.drawable.autopulse0002, R.drawable.autopulse0003, R.drawable.autopulse0004, R.drawable.autopulse0005, R.drawable.autopulse0006, R.drawable.autopulse0007, R.drawable.autopulse0008, R.drawable.autopulse0009, R.drawable.autopulse0010, R.drawable.autopulse0011, R.drawable.autopulse0012, R.drawable.autopulse0013, R.drawable.autopulse0014, R.drawable.autopulse0015, R.drawable.autopulse0016, R.drawable.autopulse0017, R.drawable.autopulse0018, R.drawable.autopulse0019, R.drawable.autopulse0020 };
    private int NUM_ANIM = 46;
    private List<String> drawableAnimGreenArray;
    //private int[] drawableAnimYellowArray = new int[]{R.drawable.autopulse0001, R.drawable.autopulse0002, R.drawable.autopulse0003, R.drawable.autopulse0004, R.drawable.autopulse0005, R.drawable.autopulse0006, R.drawable.autopulseyellow0007, R.drawable.autopulseyellow0008, R.drawable.autopulseyellow0009, R.drawable.autopulseyellow0010, R.drawable.autopulseyellow0011, R.drawable.autopulseyellow0012, R.drawable.autopulseyellow0013, R.drawable.autopulseyellow0014, R.drawable.autopulseyellow0015, R.drawable.autopulseyellow0016, R.drawable.autopulseyellow0017, R.drawable.autopulseyellow0018, R.drawable.autopulseyellow0019, R.drawable.autopulseyellow0020 };
    private List<String> drawableAnimYellowArray;
    private float currentRotation;

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

    @BindView(R.id.orientationMapButton)
    ImageView orientationMapButton;

    @BindView(R.id.orientationMapButtonView)
    ViewGroup orientationMapButtonView;

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
        mAdapter = new MapSearchListAdapter(mActionListener);
        isBookingCar = false;
        isTripStart = false;
        prevLocationDisabled = false;
        mapRadius = 0;
        tripTimestampStart = 0;
        co2 = 0f;
        currentRotation = 0f;
        drawableAnimGreenArray = new ArrayList<>();
        drawableAnimYellowArray = new ArrayList<>();
        for(int i = 0; i <= NUM_ANIM; i++){
            if(i < 10) {
                drawableAnimGreenArray.add("autopulse000" + i);
                drawableAnimYellowArray.add("autopulseyellow000" + i);
            }else {
                drawableAnimGreenArray.add("autopulse00" + i);
                drawableAnimYellowArray.add("autopulseyellow00" + i);
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_google, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mMapHelper = HdxFragmentMapHelper.newInstance(getActivity(), this);
        mMapHelper.setupMap(mMapContainer, this, savedInstanceState);

        //Setup ricerca
        setupSearch();

        //Setup animazione menu circolare
        setupCircleMenu();


        showCarsWithFeeds = false;

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Imposto il listener sull'apertura della tastiera: se appare la tastiera devo aprire la ricerca
        view.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Map listener
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);
        mPresenter.onMapIsReady();

        //Clustering
        mMap.setClustering(new ClusteringSettings().clusterOptionsProvider(new DemoClusterOptionsProvider(getResources())));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                CameraPosition oldPos = mMap.getCameraPosition();
                setRotationButton(oldPos.bearing);


                refreshMapButton.startAnimation(anim);
                refreshCars();
            }
        });

    }


    @Override
    public void onNewLocation(Location location) {
        super.onNewLocation(location);

        locationChange(location);
    }

    @Override
    public void onLocationUnavailable() {
        super.onLocationUnavailable();
        if(mMap != null)
            providerDisabled();
    }

    @Override
    public void onMyLocationChange(Location location) {


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Location listener
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onLocationChanged(Location location) {
        //mPresenter.onLocationIsReady(location.getLatitude(), location.getLongitude());

        locationChange(location);

        if(prevLocationDisabled && !isTripStart && !isBookingCar && userLocation != null){
            moveMapCameraTo(location.getLatitude(), location.getLongitude());
        }

        //markerUser
        drawUserMarker();

        prevLocationDisabled = false;

    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

        prevLocationDisabled = true;

        if(!isTripStart && !isBookingCar){
            if(userLocation != null)
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
        moveMapCameraToDefaultLocation();

        carnextMarker = null;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshCars();
            }
        }, 100);
        enabledCenterMap(false);

        if(carPreSelected != null){

            moveMapCameraTo((double) carPreSelected.latitude, (double) carPreSelected.longitude);
            showPopupCar(carPreSelected);
        }

        hasInit = true;

        if(userMarker != null) userMarker.remove();
    }

    private void locationChange(Location location){
        userLocation = location;

        //TODO: remove
        userLocation.setLatitude(45.510349);
        userLocation.setLongitude(9.093254);

        //First time
        if (!hasInit){

            //TODO Google inserire pin user
            /*pinUser = new OverlayItem("Title", "Description", userLocation);
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
            */


            if(mMap != null)
                moveMapCameraTo((double) userLocation.getLatitude(), (double) userLocation.getLongitude());


            refreshCars();
        }

        hasInit = true;

        //TODO Google
        //pinUser = new OverlayItem("Title", "Description", userLocation);



        if(carPreSelected != null){

            if(mMap != null)
                moveMapCameraTo((double) carPreSelected.latitude, (double) carPreSelected.longitude);

            showPopupCar(carPreSelected);
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
        moveMapCameraToPoitWithZoom((double) searchItem.latitude, (double) searchItem.longitude, 17);

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


            ad.add(R.drawable.ic_compass);
            ad.add(R.drawable.ic_center);
            ad.add(R.drawable.ic_referesh);
            ad.add(R.drawable.ic_cars);

            ad.add(R.drawable.ic_cars);
            ad.add(R.drawable.ic_referesh);
            ad.add(R.drawable.ic_center);
            ad.add(R.drawable.ic_compass);
            ad.add(R.drawable.ic_cars);
            ad.add(R.drawable.ic_referesh);
            ad.add(R.drawable.ic_center);
            ad.add(R.drawable.ic_compass);
            ad.add(R.drawable.ic_cars);
            ad.add(R.drawable.ic_referesh);
            ad.add(R.drawable.ic_center);
            ad.add(R.drawable.ic_compass);


            circularLayout.setAdapter(ad);
            circularLayout.setChildrenCount(16);
            circularLayout.setOffsetY(-13);
            circularLayout.setOffsetX(86);
            circularLayout.setRadius(60);
            circularLayout.setChildrenPinned(true);

        }else{
            roundMenuMapView.setVisibility(View.VISIBLE);
            roundMenuFeedsView.setVisibility(View.GONE);

            orientationMapButtonView.setTranslationX(-157.0f);
            orientationMapButtonView.setTranslationY(12.0f);

            centerMapButtonView.setTranslationX(-100.0f);
            centerMapButtonView.setTranslationY(65.0f);

            refreshMapButtonView.setTranslationX(-65.0f);
            refreshMapButtonView.setTranslationY(100.0f);

            carFeedMapButtonView.setTranslationX(-20.0f);
            carFeedMapButtonView.setTranslationY(100.0f);
        }
    }
    private void onCircleMenuClick(int i){
        switch (i){
            case 0: //Compass
                onOrientationMap();
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
            mPresenter.refreshCars(getActivity(), (float) mMap.getCameraPosition().target.latitude, (float) mMap.getCameraPosition().target.longitude, getFixMapRadius());
        }
    }

    private void setRotationButton(float rotation){

        orientationMapButton.setRotation(-rotation);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mappa
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void drawUserMarker(){

        if(userMarker != null) userMarker.remove();

        if(userLocation != null){
            com.androidmapsextensions.Marker mMarkerUser = mMap.addMarker(new MarkerOptions().position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude())));
            mMarkerUser.setIcon(getBitmapDescriptor(R.drawable.ic_user));
            mMarkerUser.setClusterGroup(ClusterGroup.NOT_CLUSTERED);
            userMarker = mMarkerUser;
        }

    }

    private void refreshCars(){


        refreshMapButton.startAnimation(anim);

        mapRadius = getMapRadius();

        if(mapRadius > 35000){

            carnextMarker = null;

            if(userMarker != null) userMarker.remove();

            if(poiMarkers != null)
                removeMarkers(poiMarkers);

            if(feedsMarker != null)
                removeMarkers(feedsMarker);

            if(poiCityMarkers != null)
                removeMarkers(poiCityMarkers);

            poiCityMarkers = new ArrayList<>();

            mPresenter.loadCity(getActivity());


        }else {

            drawUserMarker();

            if(poiCityMarkers != null)  removeMarkers(poiCityMarkers);

            if(getMapCenter().longitude > 0) {
                try {
                    mPresenter.refreshCars(getActivity(), (float) getMapCenter().latitude, (float) getMapCenter().longitude, getFixMapRadius());
                } catch (NullPointerException e) {
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
        }else{ //Cluster

            zoomCarmeraIn(marker.getPosition().latitude, marker.getPosition().longitude);
        }

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

    //Metodo per resettare l'orientamento della mappa se l'utente l'ha ruotata
    private void orientationMap(){

        CameraPosition oldPos = mMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(0.0f).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));

        orientationMapButton.setRotation(0.0f);

        //setRotationButton(0.0f);
    }

    //Recupero il centro della mappa
    private LatLng getMapCenter(){
        return mMap.getCameraPosition().target;
    }

    //Abilito / disabilito pulsante per centrare la mappa
    private void enabledCenterMap(boolean enable){

        if(enable){
            centerMapButton.setAlpha(1.0f);
            if(mPresenter.isFeeds) ad.centerAlpha = false;
        }else{
            centerMapButton.setAlpha(.4f);
            if(mPresenter.isFeeds) ad.centerAlpha = true;
        }

        if(mPresenter.isFeeds){
            circularLayout.init();
        }
    }

    //Metodo richiamato per centrare la mappa. Se la localizzazione non è attiva, viene avvisato l'utente
    private void centerMap(){

        if(userLocation != null) {
            moveMapCameraTo(userLocation.getLatitude(), userLocation.getLongitude());
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

            com.androidmapsextensions.Marker markerCity = mMap.addMarker(new MarkerOptions().position(new LatLng(cA.informations.address.latitude, cA.informations.address.longitude)));
            markerCity.setIcon(getBitmapDescriptor(R.drawable.ic_cluster));
            markerCity.setClusterGroup(ClusterGroup.NOT_CLUSTERED);
            markerCity.setData(cA);

            poiCityMarkers.add(markerCity);


            //Disegno i componenti grafici
            final com.androidmapsextensions.Marker finalMarkerCarCity = markerCity;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    try {
                        finalMarkerCarCity.setIcon(getBitmapDescriptor(makeBasicMarker(bitmap)));
                    }catch (NullPointerException e){}

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
        if(poiMarkers != null)
            removeMarkers(poiMarkers);

        if(poiCityMarkers != null)
            removeMarkers(poiCityMarkers);


        poiMarkersToAdd = new ArrayList<>();



        for(final Car car : carsList){
            //Verifico che la macchina sia in status = operative
            if(car.status.equals("operative")) {
                int icon_marker = R.drawable.ic_auto;


                //Verifico se la vettura è la più vicina oppure se è una vettura prenotata
                if(car.id.equals(carnext_id) || ((isBookingCar || isTripStart) && car.id.equals(carSelected.id))){
                    icon_marker = R.drawable.autopulse0001;
                }

                //Creo il marker
                MarkerOptions markerCar = new MarkerOptions().position(new LatLng(car.latitude, car.longitude));
                markerCar.icon(getBitmapDescriptor(icon_marker));
                markerCar.data(car);


                poiMarkersToAdd.add(markerCar);

            }
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

        poiMarkers = new ArrayList<>();

       boolean bookedCarFind = false;
        for(MarkerOptions markerCar : poiMarkersToAdd){
            com.androidmapsextensions.Marker myMarker = mMap.addMarker(markerCar);
            myMarker.setClusterGroup(ClusterGroup.DEFAULT);
            poiMarkers.add(myMarker);

            Car car = (Car) markerCar.getData();

            //Verifico se è attiva una prenotazione e se la targa dell'overley corrisponde a quella della macchina prenotata
            if(isBookingCar || isTripStart){
                if(car.id.equals(carSelected.id)) {
                    carbookingMarker = myMarker;
                    bookedCarFind = true;
                }
            }
        }

        //Se è attiva una prenotazione, ma la macchina non è presente tra i risultati restituiti dal server aggiungo la macchina alla lista
        if((isBookingCar || isTripStart) && !bookedCarFind){

            //Creo il marker
            MarkerOptions markerCar = new MarkerOptions().position(new LatLng(carSelected.latitude, carSelected.longitude));
            markerCar.icon(getBitmapDescriptor(R.drawable.autopulse0001));
            markerCar.data(carSelected);
            poiMarkersToAdd.add(markerCar);

            com.androidmapsextensions.Marker myMarker = mMap.addMarker(markerCar);
            poiMarkers.add(myMarker);

            carbookingMarker = myMarker;
        }

        //Ciclo i marker disegnati per trovare l'auto vicina
          if(poiMarkers != null && poiMarkers.size() > 0){
                for(com.androidmapsextensions.Marker markerNext : poiMarkers){
                    if(((Car) markerNext.getData()).id.equals(carnext_id)){
                        carnextMarker = markerNext;
                    }
                }

                setMarkerAnimation();
            }else{
                carnextMarker = null;
            }

          setMarkerAnimation();
    }

    //Metodo per nascondere i pin sulla mappa (richiamato in genere dal pulsante del menu radiale)
    private void hidePoiMarkers(){

        removeMarkers(poiMarkers);

        //setMarkerAnimation();
    }

    //Metodo richiamato quando si preme sul pin di un macchina
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
            moveMapCameraToPoitWithZoom((double) car.latitude, (double) car.longitude, 17);
            showPopupCar(car);
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

        if(userLocation != null) {

            for (Car car : carsList) {

                float dist = getDistance(car);

                if(dist < distance) {
                    distance = dist;
                    car_id = car.id;
                    carNext = car;
                }
            }
        }

        carnext_id = car_id;
    }

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

                                List<String> drawableAnimArray = null;

                                //Verifico se una prenotazione è attiva: il colore dell'animazione è giallo se c'è una prenotazione, altrimenti verde
                                if (isBookingCar || isTripStart)
                                    drawableAnimArray = drawableAnimYellowArray;
                                else drawableAnimArray = drawableAnimGreenArray;

                                //Ogni x millisecondi cambio il frame
                                if (isBookingCar || isTripStart) {

                                    if (carbookingMarker != null)
                                        try {
                                            carbookingMarker.setIcon(getBitmapDescriptor(resizeMapIcons(drawableAnimArray.get(currentDrawable), 500, 500)));
                                        }catch (NullPointerException e){
                                            carbookingMarker = null;
                                        }
                                    if (carnextMarker != null)
                                        try {
                                            carnextMarker.setIcon(getBitmapDescriptor(R.drawable.ic_auto));
                                        }catch (NullPointerException e){
                                            carnextMarker = null;
                                        }
                                } else {

                                    if (carbookingMarker != null)
                                        try {
                                            carbookingMarker.setIcon(getBitmapDescriptor(R.drawable.ic_auto));
                                        }catch (NullPointerException e){
                                            carbookingMarker = null;
                                        }
                                    if (carnextMarker != null) {

                                        try {
                                            carnextMarker.setIcon(getBitmapDescriptor(resizeMapIcons(drawableAnimArray.get(currentDrawable), 500, 500)));
                                        }catch (NullPointerException e){
                                            carnextMarker = null;
                                        }

                                    }
                                }


                                if (currentDrawable < drawableAnimArray.size() - 1)
                                    currentDrawable++;
                                else currentDrawable = 0;
                            }

                        }
                    });
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
    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName()));
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

    //Metodo per inserire un'icona sovrapposta al marker base (cerchio giallo con bordo verde)
    public Drawable makeBasicMarker(Bitmap bitmap) {
        Drawable[] layers = new Drawable[2];
        layers[0] = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_cluster));

        layers[1] = new BitmapDrawable(getResources(), tintImage(bitmap));
        LayerDrawable ld = new LayerDrawable(layers);
        ld.setLayerInset(1, 10, 10, 10, 10); // xx would be the values needed so bitmap ends in the upper part of the image
        return ld;
    }

    //Metodo per modificare il colore di una bitmap
    public  Bitmap tintImage(Bitmap bitmap) {
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
            if(!addresses.isEmpty())
                address = addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality();
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

        carSelected = car;

        carFeedMapButton.setAlpha(1.0f);
        showCarsWithFeeds = true;

        // Animazione
        popupCarView.setVisibility(View.VISIBLE);
        popupCarView.animate().translationY(0);

        //Popolo i dati dell'interfaccia

        //Targa
        plateTextView.setText(car.id);

        //Autonomia
        autonomyTextView.setText(Html.fromHtml(String.format(getString(R.string.maps_autonomy_label), (int) car.autonomy)));

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
                distanceString = String.format(getString(R.string.maps_distance_label), (int) distance);
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

    //Metodo per chiudere il popup che mostra le informazioni della macchina
    private void closePopup(){
        mPresenter.setCarSelected(null);
        popupCarView.animate().translationY(popupCarView.getHeight());
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

        if(carSelected != null){
            if(userLocation != null){
                //Calcolo la distanza
                if(getDistance(carSelected) <= 50){ //TODO: valore a 50
                    //Procediamo con le schermate successive
                    onClosePopup();
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

    //Metodo per verificare se è possibile prenotare la macchina (utente autenticato)
    private void checkBookingCar(){
        if(mPresenter.isAuth())
            bookingCar();
        else{
            loginAlert();
        }
    }

    //Metodo richiamato per prenotare una macchina selezionata
    private void bookingCar(){
        mPresenter.bookingCar(carSelected, getContext());
    }

    //Metodo per mostrare le informazioni sulla prenotazione
    private void reservationInfo(Car mCar, Reservation mReservation){
        isBookingCar = true;
        ad.carAlpha = false;
        circularLayout.init();

        carSelected = mCar;
        reservation = mReservation;

        //removeMarkers(poiMarkers);
        if(poiMarkersToAdd == null) poiMarkersToAdd = new ArrayList<>();
        if(poiMarkers == null) poiMarkers = new ArrayList<>();

        //Aggiungo la macchina
        MarkerOptions markerCar = new MarkerOptions().position(new LatLng(mCar.latitude, mCar.longitude));
        markerCar.icon(getBitmapDescriptor(R.drawable.autopulse0001));
        markerCar.data(mCar);
        poiMarkersToAdd.add(markerCar);

        com.androidmapsextensions.Marker myMarker = mMap.addMarker(markerCar);
        poiMarkers.add(myMarker);


        carbookingMarker = myMarker;

        moveMapCameraToPoitWithZoom((double) carSelected.latitude, (double) carSelected.longitude, 17);

        setMarkerAnimation();

        onClosePopup();
        openViewBookingCar();
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
                                    tripDurationTextView.setText(hhStr + ":" + mnStr + ":" + secStr);
                                else if (countDownTimer != null) countDownTimer.cancel();

                            }
                        });
                    }
                }
            }, 1000, 1000);
        }
        //----

        //Popolo le informazioni
        userPinTextView.setText(String.format(getString(R.string.booking_userpin_label), ""+pinUser));
        bookingPlateTextView.setText(String.format(getString(R.string.booking_plate_label), plateBooking));


        if(isTripStart){
            bookingTitleTextView.setText(getString(R.string.booking_tripactive_label));
            bookingAddressTextView.setText(getString(R.string.booking_durationtrip_label));
            timeIconImageView.setImageDrawable(getIconMarker(R.drawable.ic_time_2));
        }else{
            bookingTitleTextView.setText(getString(R.string.booking_active_label));
            bookingAddressTextView.setText(addressBooking);
            expiringTimeTextView.setText(Html.fromHtml(String.format(getString(R.string.booking_expirationtime), timingBookin)));
            timeIconImageView.setImageDrawable(getIconMarker(R.drawable.ic_time));
        }

        //Apro le informazioni
        bookingCarView.setVisibility(View.VISIBLE);


        //Verifico se la corsa è iniziata: nel caso nascondo le informazioni non necessarie
        if(isTripStart){
            expiringTimeTextView.setVisibility(View.GONE);
            tripDurationTextView.setVisibility(View.VISIBLE);
            openButtonBookingView.setVisibility(View.GONE);
        }else{
            expiringTimeTextView.setVisibility(View.VISIBLE);
            tripDurationTextView.setVisibility(View.GONE);
        }


        for(com.androidmapsextensions.Marker marker : poiMarkers){
            if(((Car) marker.getData()).id.equals(plateBooking)){
                carbookingMarker = marker;
            }
        }

        carFeedMapButton.setAlpha(1.0f);
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

        //Nascondo le informazioni della prenotazione cancellata
        if(bookingCarView != null)
            bookingCarView.setVisibility(View.GONE);

        //Tolgo l'animazione al pin
        setMarkerAnimation();
    }

    //Metodo quando arriva dal server la conferma che la prenotaizone è stata annullata
    private void confirmDeletedCar(){
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

    //Metodo per nascondere le informazioni delle prenotazione
    private void hideReservationInfo(){
        isBookingCar = false;
        carSelected = null;
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
        isTripStart = true;
        tripTimestampStart = timestamp_start;
        carSelected = car;

        //removeMarkers(poiMarkers);
        if(poiMarkersToAdd == null) poiMarkersToAdd = new ArrayList<>();
        if(poiMarkers == null) poiMarkers = new ArrayList<>();


        //Aggiungo la macchina
        MarkerOptions markerCar = new MarkerOptions().position(new LatLng(car.latitude, car.longitude));
        markerCar.icon(getBitmapDescriptor(R.drawable.autopulse0001));
        markerCar.data(car);
        poiMarkersToAdd.add(markerCar);

        com.androidmapsextensions.Marker myMarker = mMap.addMarker(markerCar);
        poiMarkers.add(myMarker);


        carbookingMarker = myMarker;


        moveMapCameraToPoitWithZoom((double) car.latitude, (double) car.longitude, 17);


        setMarkerAnimation();

        openViewBookingCar();
    }

    //Metodo richiamato per togliere tutte le informazioni della corsa attiva
    private void hideTripInfo(){
        isTripStart = false;
        isBookingCar = false;
        carSelected = null;
        closeViewBookingCar();
    }

    private void showNotification(int start, int end){
        int diffTime = (int) (end - start);

        co2 = ((float) diffTime)/60/60*17*106;  //((minuti÷60)×17)×106

        ((MapActivity) getActivity()).showNotification(String.format(getString(R.string.tripend_notification_label), diffTime/60), mNotificationListener);
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
            markerFeed.setIcon(getBitmapDescriptor(R.drawable.ic_cluster));
            markerFeed.setClusterGroup(ClusterGroup.NOT_CLUSTERED);
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

            moveMapCameraToPoitWithZoom((double) carNext.latitude, (double) carNext.longitude, 17);
            showPopupCar(carNext);
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

        //Zoom sulla mappa
        moveMapCameraToPoitWithZoom((double) feed.informations.address.latitude, (double) feed.informations.address.longitudef, 17);

        //Immagine copertina
        ImageUtils.loadImage(feedImageView, feed.media.images.image.uri);

        //Overlay copertina
        feedOverlayView.setBackgroundColor(Color.parseColor(feed.appearance.color.rgb));

        //Advantage
        feedTriangleImageView.setColorFilter(Color.parseColor(feed.appearance.color.rgb));
        if(feed.informations.advantage_top.isEmpty()) {
            feedTriangleView.setVisibility(View.GONE);
        }else {
            feedTriangleView.setVisibility(View.VISIBLE);
            feedAdvantageTextView.setText(feed.informations.advantage_top);
        }

        //Icona
        ImageUtils.loadImage(feedIconImageView, feed.category.media.images.icon.uri);
        GradientDrawable backgroundShape = (GradientDrawable) feedIconImageView.getBackground();
        backgroundShape.setColor(Color.parseColor(feed.appearance.color.rgb));

        //Data
        feedDateTextView.setText(feed.informations.date.friendly);

        //Launch title
        if(feed.informations.launch_title.isEmpty()){
            feedLaunchTitleTextView.setVisibility(View.GONE);
        }else{
            feedLaunchTitleTextView.setVisibility(View.VISIBLE);
            feedLaunchTitleTextView.setText(feed.informations.launch_title);
            feedLaunchTitleTextView.setTextColor(Color.parseColor(feed.appearance.color.rgb));
        }

        //Titolo
        feedTitleTextView.setText(feed.title);

        //Location
        feedLocationTextView.setText(feed.informations.location + ", " +  feed.informations.address.friendly +  ", " + feed.informations.city.name);

        //Advantage bottom
        if(feed.informations.advantage_bottom.isEmpty()){
            feedAdvantageBottomTextView.setVisibility(View.GONE);
        }else{
            feedAdvantageBottomTextView.setVisibility(View.VISIBLE);
            feedAdvantageBottomTextView.setText(feed.informations.advantage_bottom);

            if(feed.informations.sponsored.equals("true"))
                feedAdvantageBottomTextView.setTextColor(Color.parseColor(feed.appearance.color.rgb));
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
        refreshMap();
    }

    @OnClick(R.id.centerMapButtonView)
    public void onCenterMap() {
        centerMap();
    }

    @OnClick(R.id.orientationMapButtonView)
    public void onOrientationMap() {
        orientationMap();
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
        checkOpenDoor();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void showCars(List<Car> carsList) {
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
    }

    @Override
    public void openReservationNotification() {
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
    }



    //Classe per customizzare il cluster
    public class DemoClusterOptionsProvider implements ClusterOptionsProvider {

        private final int[] res = {R.drawable.ic_cluster, R.drawable.ic_cluster, R.drawable.ic_cluster, R.drawable.ic_cluster, R.drawable.ic_cluster};

        private final int[] forCounts = {10, 100, 1000, 10000, Integer.MAX_VALUE};

        private Bitmap[] baseBitmaps;
        private LruCache<Integer, BitmapDescriptor> cache = new LruCache<Integer, BitmapDescriptor>(128);

        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Rect bounds = new Rect();

        private ClusterOptions clusterOptions = new ClusterOptions().anchor(0.5f, 0.5f);

        public DemoClusterOptionsProvider(Resources resources) {
            baseBitmaps = new Bitmap[res.length];
            for (int i = 0; i < res.length; i++) {
                baseBitmaps[i] = BitmapFactory.decodeResource(resources, res[i]);
            }
            paint.setColor(ContextCompat.getColor(getContext(), R.color.darkpastelgreen));
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(15 * getResources().getDisplayMetrics().density);
        }

        @Override
        public ClusterOptions getClusterOptions(List<com.androidmapsextensions.Marker> markers) {

            int markersCount = markers.size();
            BitmapDescriptor cachedIcon = cache.get(markersCount);
            if (cachedIcon != null) {
                return clusterOptions.icon(cachedIcon);
            }

            Bitmap base;
            int i = 0;
            do {
                base = baseBitmaps[i];
            } while (markersCount >= forCounts[i++]);

            Bitmap bitmap = base.copy(Bitmap.Config.ARGB_8888, true);

            String text = String.valueOf(markersCount);
            paint.getTextBounds(text, 0, text.length(), bounds);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            float x = bitmap.getWidth() / 2.0f;
            float y = (bitmap.getHeight() - bounds.height()) / 2.0f - bounds.top;

            Canvas canvas = new Canvas(bitmap);
            canvas.drawText(text, x, y, paint);


            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
            cache.put(markersCount, icon);

            return clusterOptions.icon(icon);
        }
    }
}
