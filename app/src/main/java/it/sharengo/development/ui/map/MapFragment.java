package it.sharengo.development.ui.map;

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
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.x.circlelayout.CircleLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringUtils;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.clustering.StaticCluster;
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
import java.lang.reflect.Type;
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
import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.Feed;
import it.sharengo.development.data.models.Reservation;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.fragments.BaseMvpFragment;
import it.sharengo.development.ui.components.CustomDialogClass;
import it.sharengo.development.ui.map.CircleLayout.MyCircleLayoutAdapter;
import it.sharengo.development.utils.ImageUtils;

import static android.content.Context.MODE_PRIVATE;
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

    public static final String ARG_TYPE = "ARG_TYPE";

    private int type = 0;

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
    private FolderOverlay feedsMarker;
    private FolderOverlay poiCityMarkers;
    private RotateAnimation anim;
    private String carnext_id;
    private Car carNext;
    private Car carSelected;
    private OverlayItem pinUser;
    private boolean searchViewOpen = false;
    private Marker carnextMarker, carbookingMarker;
    private int currentDrawable = 0; //frame dell'animazione della macchiana più vicina
    private int[] drawableAnimGreenArray = new int[]{R.drawable.autopulse0001, R.drawable.autopulse0002, R.drawable.autopulse0003, R.drawable.autopulse0004, R.drawable.autopulse0005, R.drawable.autopulse0006, R.drawable.autopulse0007, R.drawable.autopulse0008, R.drawable.autopulse0009, R.drawable.autopulse0010, R.drawable.autopulse0011, R.drawable.autopulse0012, R.drawable.autopulse0013, R.drawable.autopulse0014, R.drawable.autopulse0015, R.drawable.autopulse0016, R.drawable.autopulse0017, R.drawable.autopulse0018, R.drawable.autopulse0019, R.drawable.autopulse0020 };
    private int[] drawableAnimYellowArray = new int[]{R.drawable.autopulse0001, R.drawable.autopulse0002, R.drawable.autopulse0003, R.drawable.autopulse0004, R.drawable.autopulse0005, R.drawable.autopulse0006, R.drawable.autopulseyellow0007, R.drawable.autopulseyellow0008, R.drawable.autopulseyellow0009, R.drawable.autopulseyellow0010, R.drawable.autopulseyellow0011, R.drawable.autopulseyellow0012, R.drawable.autopulseyellow0013, R.drawable.autopulseyellow0014, R.drawable.autopulseyellow0015, R.drawable.autopulseyellow0016, R.drawable.autopulseyellow0017, R.drawable.autopulseyellow0018, R.drawable.autopulseyellow0019, R.drawable.autopulseyellow0020 };
    private Timer timer;
    private Timer timerTripDuration;
    private CountDownTimer countDownTimer;
    private SearchItem currentSearchItem;
    private boolean searchItemSelected = false;
    private boolean isBookingCar = false;
    private boolean isTripStart = false;
    private int tripTimestampStart = 0;
    private Reservation reservation;
    private Trip trip;
    private Car carPreSelected;
    private Feed feedSelected;
    private boolean showCarsWithFeeds;

    private int touchX;
    private int touchY;
    private float deltaX;
    private float deltaY;
    private Handler mHandler = new Handler();
    private int eventMotionType = -1;
    private static final int FINGER_STOP_THRESHOLD = 500;
    private double deltaAngle = 3;

    private float currentRotation = 0f;
    private float co2 = 0f;

    private MapSearchListAdapter mAdapter;
    private MapSearchListAdapter.OnItemActionListener mActionListener = new MapSearchListAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(SearchItem searchItem) {
            if(!searchItem.type.equals("none"))
                setSearchItemSelected(searchItem);
        }
    };

    private MyCircleLayoutAdapter ad;
    private MyCircleLayoutAdapter.OnItemActionListener mActionCircleListener = new MyCircleLayoutAdapter.OnItemActionListener() {
        @Override
        public void onItemClick(int index) {
            onCircleMenuClick(index);
        }
    };

    private View.OnClickListener mNotificationListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Navigator.launchTripEnd(MapFragment.this, co2);
        }
    };



    @BindView(R.id.mapView)
    MapView mMapView;

    @BindView(R.id.centerMapButton)
    ImageView centerMapButton;

    @BindView(R.id.centerMapButtonView)
    ViewGroup centerMapButtonView;

    @BindView(R.id.refreshMapButton)
    ImageView refreshMapButton;

    @BindView(R.id.refreshMapButtonView)
    ViewGroup refreshMapButtonView;

    @BindView(R.id.mapOverlayView)
    View mapOverlayView;

    @BindView(R.id.popupCarView)
    View popupCarView;

    @BindView(R.id.popupFeedView)
    View popupFeedView;

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

    @BindView(R.id.orientationMapButtonView)
    ViewGroup orientationMapButtonView;

    @BindView(R.id.carFeedMapButton)
    ImageView carFeedMapButton;

    @BindView(R.id.carFeedMapButtonView)
    ViewGroup carFeedMapButtonView;

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

    @BindView(R.id.timeIconImageView)
    ImageView timeIconImageView;

    @BindView(R.id.expiringTimeTextView)
    TextView expiringTimeTextView;

    @BindView(R.id.tripDurationTextView)
    TextView tripDurationTextView;

    @BindView(R.id.expiringTimeView)
    ViewGroup expiringTimeView;

    @BindView(R.id.openButtonBookingView)
    ViewGroup openButtonBookingView;

    @BindView(R.id.bookingTitleTextView)
    TextView bookingTitleTextView;

    /*@BindView(R.id.notificationView)
    ViewGroup notificationView;*/

    @BindView(R.id.frikFrak)
    View frikFrak;

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

    @BindView(R.id.roundMenuMapView)
    ViewGroup roundMenuMapView;

    @BindView(R.id.roundMenuFeedsView)
    ViewGroup roundMenuFeedsView;

    @BindView(R.id.circularLayout)
    CircleLayout circularLayout;

    public static MapFragment newInstance(int type) {
        MapFragment fragment = new MapFragment();
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

        mAdapter = new MapSearchListAdapter(mActionListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_map, container, false);

        mUnbinder = ButterKnife.bind(this, view);

        ad = new MyCircleLayoutAdapter(mActionCircleListener);
        ad.animationRefresh = true;

        //setUpMap();

        //carSelected = null;

        //Rotate animation - refresh button
        anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(700);

        showCarsWithFeeds = false;

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
                        Log.w("refreshCars","onScroll");
                        refreshCars();

                        setRotationButton(mMapView.getMapOrientation());
                    }
                    return false;
                }

                @Override
                public boolean onZoom(ZoomEvent event) {
                    Log.w("onZoom","onZoom");
                    Log.w("hasInit",": "+hasInit);
                    if(hasInit) {

                        refreshMapButton.startAnimation(anim);

                        Log.w("refreshCars","onZoom");
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
        //circularLayout.pinn

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

                /*int widthN = notificationView.getWidth();
                int heightN = notificationView.getHeight();

                if (widthN > 0 && heightN > 0) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        notificationView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        notificationView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    notificationView.setTranslationY(notificationView.getHeight());
                }*/
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




        //Animazion cerchio CCC
        /*if(mPresenter.isFeeds) {

            frikFrak.setOnTouchListener(new View.OnTouchListener() {


                public boolean onTouch(View arg0, MotionEvent event) {


                    boolean returnEvent = true;
                    Log.w("motionType",": "+event.getAction());

                    switch (event.getAction() & MotionEvent.ACTION_MASK) {

                        case MotionEvent.ACTION_DOWN:
                            //Log.w("eventMotionType",": "+eventMotionType);

                            returnEvent = false;
                            break;

                        case MotionEvent.ACTION_MOVE:
                            //Log.w("eventMotionType","SWIPO");
                            touchX = (int) event.getX();
                            touchY = (int) event.getY();

                            deltaX = touchX - (frikFrak.getX() + frikFrak.getWidth());
                            deltaY = touchY - (frikFrak.getY() + frikFrak.getHeight());


                            mHandler.removeCallbacksAndMessages(null);
                            if (event.getActionMasked() != MotionEvent.ACTION_UP) {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        deltaX = 0;
                                        deltaY = 0;
                                    }
                                }, FINGER_STOP_THRESHOLD);
                            }

                            double prevDeltaAngle = deltaAngle;

                            deltaAngle = 3;
                            deltaAngle -= -atan2(deltaX, deltaY);

                            if (prevDeltaAngle < deltaAngle) {

                                orientationMapButtonView.setTranslationX(-157.0f);
                                orientationMapButtonView.setTranslationY(12.0f);

                                centerMapButtonView.setTranslationX(-100.0f);
                                centerMapButtonView.setTranslationY(65.0f);

                                refreshMapButtonView.setTranslationX(-65.0f);
                                refreshMapButtonView.setTranslationY(100.0f);

                                carFeedMapButtonView.setTranslationX(-20.0f);
                                carFeedMapButtonView.setTranslationY(100.0f);
                            } else {

                                orientationMapButtonView.setTranslationX(-1.0f);
                                orientationMapButtonView.setTranslationY(-1.0f);

                                centerMapButtonView.setTranslationX(-1.0f);
                                centerMapButtonView.setTranslationY(-1.0f);

                                refreshMapButtonView.setTranslationX(-1.0f);
                                refreshMapButtonView.setTranslationY(-1.0f);

                                carFeedMapButtonView.setTranslationX(-1.0f);
                                carFeedMapButtonView.setTranslationY(-1.0f);
                            }


                            break;

                    }

                    eventMotionType = event.getAction();
                    //Log.w("returnEvent",": "+returnEvent);
                    return returnEvent;
                }
            });
        }*/

        if(!mPresenter.isFeeds){
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

        Log.w("INIT","onLocationChanged");

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

            Log.w("refreshCars","onLocationChanged");
            refreshCars();
        }

        hasInit = true;
        //overlay.setBearing(location.getBearing());
        //overlay.setAccuracy((int)location.getAccuracy());
        //overlay.setLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
        //mMapView.invalidate();


        pinUser = new OverlayItem("Title", "Description", userLocation);

        enabledCenterMap(true);

        if(carPreSelected != null){
            mMapView.getController().setCenter(new GeoPoint(carPreSelected.latitude, carPreSelected.longitude));
            mMapView.getController().zoomIn();
            showPopupCar(carPreSelected);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

        Log.w("INIT","onProviderDisabled");

        userLocation = null;

        if (!hasInit){
            mMapView.getController().setCenter(defaultLocation);
            mMapView.getController().setZoom(ZOOM_B);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.w("refreshCars","onProviderDisabled");
                    refreshCars();
                }
            }, 100);

        }
        enabledCenterMap(false);

        if(carPreSelected != null){
            mMapView.getController().setCenter(new GeoPoint(carPreSelected.latitude, carPreSelected.longitude));
            mMapView.getController().zoomIn();
            showPopupCar(carPreSelected);
        }

        hasInit = true;
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
                if(ad.carAlpha) ad.carAlpha = false;
                else ad.carAlpha = true;
                circularLayout.init();
                break;
        }
    }

    private void loadsCars(){
        mPresenter.loadCars((float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), getFixMapRadius());
    }

    private void refreshCars(){


        refreshMapButton.startAnimation(anim);

        int mapRadius = getMapRadius();

        if(mapRadius > 35000){

            if(poiMarkers != null)
                mMapView.getOverlays().remove(poiMarkers);

            if(feedsMarker != null)
                mMapView.getOverlays().remove(feedsMarker);

            if(poiCityMarkers != null)
                mMapView.getOverlays().remove(poiCityMarkers);

            poiCityMarkers = new FolderOverlay();

            mPresenter.loadCity(getActivity());

            mMapView.getOverlays().add(poiCityMarkers);
            mMapView.invalidate();

        }else {

            if(poiCityMarkers != null)  mMapView.getOverlays().remove(poiCityMarkers);

            if(getMapCenter().getLongitude() > 0) {
                try {
                    mPresenter.refreshCars(getActivity(), (float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), getFixMapRadius());
                } catch (NullPointerException e) {
                }
            }
        }

    }

    public Drawable makeBasicMarker(Bitmap bitmap) {
        Drawable[] layers = new Drawable[2];
        layers[0] = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_cluster));

        layers[1] = new BitmapDrawable(getResources(), tintImage(bitmap));
        LayerDrawable ld = new LayerDrawable(layers);
        ld.setLayerInset(1, 10, 10, 10, 10); // xx would be the values needed so bitmap ends in the upper part of the image
        return ld;
    }

    public  Bitmap tintImage(Bitmap bitmap) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity(), R.color.mediumseagreen), PorterDuff.Mode.SRC_ATOP));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
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
            if(mPresenter.isFeeds) ad.centerAlpha = false;
        }else{
            centerMapButton.setAlpha(.4f);
            if(mPresenter.isFeeds) ad.centerAlpha = true;
        }

        if(mPresenter.isFeeds){
            circularLayout.init();
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
                    carNext = car;
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
                Navigator.launchLogin(MapFragment.this, Navigator.REQUEST_LOGIN_MAPS);
                getActivity().finish();
            }
        });
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

        //Salvo la ricerca appena effettuata solo se sono un utente loggato
        if(searchItem.type.equals("address") && mPresenter.isAuth())
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
        //Mostro preferiti + storisco nella view dei risultati (solo se l'utente è loggato)
        if(mPresenter.isAuth())
            mPresenter.getSearchItems("", getContext(), getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE));
    }

    //Salvo l'ultima ricerca fatta
    private void saveLastAndFavouriteSearch(SearchItem searchItem){
        //PreferencesDataSource aa = new PreferencesDataSource(getActivity().getSharedPreferences("aa", 0));
        mPresenter.saveSearchResultOnHistoric(getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE), searchItem);
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

        //Procediamo con le schermate successive TODO
        //onClosePopup();
        //mPresenter.openDoor(carSelected, "open");
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


        //Log.w("trip",": "+);

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
                                Log.w("unixTime", ": " + unixTime);
                                Log.w("diffTime", ": " + diffTime);

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

        //refreshCars();

        for(Marker marker : poiMarkers.getItems()){
            if(marker.getTitle().equals(plateBooking)){
                carbookingMarker = marker;
            }
        }

        carFeedMapButton.setAlpha(1.0f);
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
        if(bookingCarView != null)
            bookingCarView.setVisibility(View.GONE);

        //Tolgo l'animazione al pin
        setMarkerAnimation();
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

    /*@OnTextChanged(R.id.searchEditText)
    public void searchEditText(){
        if(!searchItemSelected)
            initMapSearch();
        else searchItemSelected = false;
    }*/

    private Timer timerEditText=new Timer();
    private final long DELAY = 500;
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

    @OnClick(R.id.refreshMapButtonView)
    public void onRefreshMap() {
        if(getMapRadius() < 35000) {
            refreshMapButton.startAnimation(anim);
            mPresenter.refreshCars(getActivity(), (float) getMapCenter().getLatitude(), (float) getMapCenter().getLongitude(), getFixMapRadius());
        }
    }

    @OnClick(R.id.closePopupButton)
    public void onClosePopup() {
        mPresenter.setCarSelected(null);
        popupCarView.animate().translationY(popupCarView.getHeight());
    }

    @OnClick(R.id.closeFeedPopupButton)
    public void onCloseFeedPopup() {
        feedSelected = null;
        popupFeedView.animate().translationY(popupFeedView.getHeight());
    }

    @OnClick(R.id.openDoorButton)
    public void onOpenDoor(){

        if(mPresenter.isAuth())
            openDoors();
        else{
            loginAlert();
        }
    }

    @OnClick(R.id.openDoorBookingButton)
    public void openDoorBookingButton(){

        if(mPresenter.isAuth())
            openDoors();
        else{
            loginAlert();
        }
    }

    @OnClick(R.id.bookingCarButton)
    public void onBookingCar(){

        if(mPresenter.isAuth())
            bookingCar();
        else{
            loginAlert();
        }
    }

    @OnClick(deleteBookingButton)
    public void onDeleteBookingButton(){
        deleteBooking();
    }

    @OnClick(R.id.feedDiscoverButton)
    public void onDiscoverClick(){
        Navigator.launchFeedsDetail(this, feedSelected);
    }

    @OnClick(R.id.feedBookingButton)
    public void onFeedBookingClick(){
        //carnext_id
        //carnextMarker
        //FEEED

        if(carNext != null && userLocation != null) {

            if(!showCarsWithFeeds)
                showPoiMarkers();

            showCarsWithFeeds = true;

            onCloseFeedPopup();

            mMapView.getController().setCenter(new GeoPoint(carNext.latitude, carNext.longitude));
            mMapView.getController().setZoom(ZOOM_C);
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

        poiMarkers = new CustomRadiusMarkerClusterer(getActivity());

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


        if(!mPresenter.isFeeds || showCarsWithFeeds || isBookingCar || isTripStart)
            showPoiMarkers();


        //Stop sull'animazione del pulsante di refresh
        anim.cancel();
        if(mPresenter.isFeeds){
            ad.animationRefresh = false;
            circularLayout.init();
        }

    }

    private void showPoiMarkers(){
        mMapView.getOverlays().add(poiMarkers);
        mMapView.invalidate();

        setMarkerAnimation();
    }

    private void hidePoiMarkers(){
        mMapView.getOverlays().remove(poiMarkers);
        mMapView.invalidate();

        setMarkerAnimation();
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

    private void onTapFeedMarker(Feed feed){

        feedSelected = feed;

        //Zoom sulla mappa
        mMapView.getController().setCenter(new GeoPoint(feed.informations.address.latitude, feed.informations.address.longitudef));
        mMapView.getController().zoomTo(ZOOM_C);

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

    @Override
    public void setFeedInters(){

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


    @Override
    public void noCarsFound() {
        //Stop sull'animazione del pulsante di refresh
        anim.cancel();
        if(mPresenter.isFeeds){
            ad.animationRefresh = false;
            circularLayout.init();
        }
    }

    @Override
    public void showFeeds(List<Feed> feedsList) {

        if(feedsMarker != null)
            mMapView.getOverlays().remove(feedsMarker);

        feedsMarker = new FolderOverlay();

        for(final Feed feed : feedsList){

            //Creo il marker
            Marker markerFeed = new Marker(mMapView);
            markerFeed.setPosition(new GeoPoint(feed.informations.address.latitude, feed.informations.address.longitudef));
            markerFeed.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            //markerFeed.setIcon(getIconMarker(R.drawable.ic_auto)); //TODO
            markerFeed.setTitle("AA"); //TODO
            markerFeed.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {

                    onTapFeedMarker(feed);
                    return true;
                }
            });

            //Disegno i componenti grafici
            final Marker finalMarkerCarCity = markerFeed;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                    /*public Drawable makeBasicMarker(Bitmap bitmap) {
        Drawable[] layers = new Drawable[2];
        layers[0] = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.ic_cluster));

        layers[1] = new BitmapDrawable(getResources(), tintImage(bitmap));
        LayerDrawable ld = new LayerDrawable(layers);
        ld.setLayerInset(1, 10, 10, 10, 10); // xx would be the values needed so bitmap ends in the upper part of the image
        return ld;
    }*/

                    finalMarkerCarCity.setIcon(new BitmapDrawable(getResources(), bitmap));
                    //Aggiungo all'array
                    feedsMarker.add(finalMarkerCarCity);
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

        }

        mMapView.getOverlays().add(feedsMarker);
        mMapView.invalidate();

        //Stop sull'animazione del pulsante di refresh
        anim.cancel();
        if(mPresenter.isFeeds){
            ad.animationRefresh = false;
            circularLayout.init();
        }

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
    public void showTripInfo(final Car car, int timestamp_start){

        isTripStart = true;
        tripTimestampStart = timestamp_start;
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
        isBookingCar = false;
        carSelected = null;
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
        carSelected = null;
        closeViewBookingCar();
    }

    @Override
    public void openTripEnd(int timestamp){
        Navigator.launchTripEnd(this, timestamp);
    }

    @Override
    public void openNotification(int start, int end){


        int diffTime = (int) (end - start);

        co2 = ((float) diffTime)/60/60*17*106;  //((minuti÷60)×17)×106

        ((MapActivity) getActivity()).showNotification(String.format(getString(R.string.tripend_notification_label), diffTime/60), mNotificationListener);
    }

    @Override
    public void openReservationNotification(){

        ((MapActivity) getActivity()).showNotification(getString(R.string.booking_timeend_label), null);
    }

    @Override
    public void openPreselectedCarPopup(Car car){
        carPreSelected = car;
    }

    @Override
    public void showCity(List<City> cityList){
        for(City cA : cityList){

            //Setto il marker
            final GeoPoint geoPoint = new GeoPoint(cA.informations.address.latitude, cA.informations.address.longitude);
            Marker markerCarCity = new Marker(mMapView);
            markerCarCity.setPosition(geoPoint);
            //markerCarCity.setIcon(getIconMarker(R.drawable.ic_cluster));
            markerCarCity.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            //Listener
            markerCarCity.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {

                    if(poiCityMarkers != null)
                        mMapView.getOverlays().remove(poiCityMarkers);

                    mMapView.invalidate();

                    mMapView.getController().setCenter(geoPoint);
                    mMapView.getController().setZoom(11);
                    mMapView.invalidate();

                    //refreshCars();


                    return true;
                }
            });



            //Disegno i componenti grafici
            final Marker finalMarkerCarCity = markerCarCity;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    finalMarkerCarCity.setIcon(makeBasicMarker(bitmap));
                    //Aggiungo all'array
                    poiCityMarkers.add(finalMarkerCarCity);
                    mMapView.invalidate();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };

            Picasso.with(getActivity()).load(cA.media.images.icon.uri).into(target);

        }

        anim.cancel();
        if(mPresenter.isFeeds){
            ad.animationRefresh = false;
            circularLayout.init();
        }
    }



    /*AAA*/
    /*Marker markerCarCity;
    private void showCityMarker(final GeoPoint geoPoint, int clusterIcon){
        markerCarCity = new Marker(mMapView);
        markerCarCity.setPosition(geoPoint);
        markerCarCity.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        //markerCarCity.setIcon(getIconMarker(clusterIcon));
        //markerCarCity.setIcon(makeBasicMarker());
        markerCarCity.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {

                if(poiCityMarkers != null)
                    mMapView.getOverlays().remove(poiCityMarkers);

                mMapView.invalidate();

                mMapView.getController().setCenter(geoPoint);
                mMapView.getController().setZoom(11);
                mMapView.invalidate();


                return true;
            }
        });

        someMethod();

        poiCityMarkers.add(markerCarCity);

    }*/


    /*private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Log.w("MAP","onBitmapLoaded");
            markerCarCity.setIcon(makeBasicMarker(bitmap));
            mMapView.invalidate();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };*/

    /*private void someMethod() {
        Log.w("MAP","someMethod");
        Picasso.with(getActivity()).load("http://universo-sharengo.thedigitalproject.it/sites/default/files/assets/images/icona_trasparente-milano.png").into(target);
    }*/
    /*AAA*/


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Class
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class CustomRadiusMarkerClusterer extends RadiusMarkerClusterer{

        public CustomRadiusMarkerClusterer(Context ctx) {
            super(ctx);
        }

        @Override
        public void renderer(ArrayList<StaticCluster> clusters, Canvas canvas, MapView mapView) {
            for (StaticCluster cluster : clusters) {
                if (cluster.getSize() == 1) {
                    //cluster has only 1 marker => use it as it is:
                    cluster.setMarker(cluster.getItem(0));
                } else {
                    //only draw 1 Marker at Cluster center, displaying number of Markers contained
                    Marker m = buildClusterMarker(cluster, mapView);
                    cluster.setMarker(m);

                    m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                            mMapView.getController().setCenter(new GeoPoint(marker.getPosition().getLatitude(), marker.getPosition().getLongitude()));
                            mMapView.getController().zoomIn();
                            return false;
                        }
                    });
                }
            }
        }
    }


}
