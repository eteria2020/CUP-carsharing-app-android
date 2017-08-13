package it.sharengo.development.ui.mapgoogle;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.androidmapsextensions.GoogleMap;
import com.androidmapsextensions.MarkerOptions;
import com.androidmapsextensions.OnMapReadyCallback;
import com.example.x.circlelayout.CircleLayout;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import it.sharengo.development.ui.mapgoogle.CircleLayout.MyCircleLayoutAdapter;

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

    /* Map */
    private List<ClusterItem> poiMarkers;
    private List<com.androidmapsextensions.Marker> feedsMarker;
    private List<com.androidmapsextensions.Marker> poiCityMarkers;

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

        locationChange(location);

        if(prevLocationDisabled && !isTripStart && !isBookingCar && userLocation != null){
            moveMapCameraTo(location.getLatitude(), location.getLongitude());
        }

        prevLocationDisabled = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Location listener
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onLocationChanged(Location location) {
        //mPresenter.onLocationIsReady(location.getLatitude(), location.getLongitude());

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

        if (!hasInit){

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshCars();
                }
            }, 100);

        }
        enabledCenterMap(false);

        if(carPreSelected != null){

            moveMapCameraTo((double) carPreSelected.latitude, (double) carPreSelected.longitude);
            //TODO Google
            //showPopupCar(carPreSelected);
        }

        hasInit = true;
    }

    private void locationChange(Location location){
        userLocation = location;
        //userLocation = new GeoPoint(45.538927, 9.168744); //TODO: remove

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

            //TODO Google
            //showPopupCar(carPreSelected);
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

    private void setSearchItemSelected(SearchItem searchItem){
        hideSoftKeyboard();

        //Muovo la mappa
        //TODO Google
        /*mMapView.getController().setCenter(new GeoPoint(searchItem.latitude, searchItem.longitude));
        mMapView.getController().zoomTo(ZOOM_A);
        mMapView.getController().animateTo(new GeoPoint(searchItem.latitude, searchItem.longitude));*/

        //Se è una targa, apro il popup
        //TODO Google
        /*if(searchItem.type.equals("plate")){
            Car car = mPresenter.findPlateByID(searchItem.display_name);
            if(car != null)
                showPopupCar(car);
        }*/

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
        //PreferencesDataSource aa = new PreferencesDataSource(getActivity().getSharedPreferences("aa", 0));
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
                //onOrientationMap();
                break;
            case 1: //Center
                //onCenterMap();
                break;
            case 2: //Refresh
                //onRefreshMap();
                ad.animationRefresh = true;
                //circularLayout.init();
                break;
            case 3: //Car
                //onShowCarOnMapClick();
                if(ad.carAlpha) ad.carAlpha = false;
                else ad.carAlpha = true;
                //circularLayout.init();
                break;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mappa
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void refreshCars(){


        refreshMapButton.startAnimation(anim);

        int mapRadius = getMapRadius();

        if(mapRadius > 35000){

            if(poiMarkers != null)
                mMap.getMarkers().remove(poiMarkers);

            if(feedsMarker != null)
                removeMarkers(feedsMarker);

            if(poiCityMarkers != null)
                removeMarkers(poiCityMarkers);

            poiCityMarkers = new ArrayList<>();

            mPresenter.loadCity(getActivity());


        }else {

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
        for (com.androidmapsextensions.Marker marker: markerList) {
            marker.remove();
        }
        markerList.clear();
    }

    //Metodo richiamato quando viene eseguito il tap su un marker presente nella mappa
    @Override
    public boolean onMarkerClick(com.androidmapsextensions.Marker marker) {

        if (marker.getData().getClass().equals(City.class)) {


            if(poiCityMarkers != null)
                removeMarkers(poiCityMarkers);

            moveMapCameraToPoitWithZoom(marker.getPosition().latitude, marker.getPosition().longitude, 10);

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
        return 700000;
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

    private void openSettings(){
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    private void drawCityMarkerOnMap(List<City> cityList){
        for(City cA : cityList){

            com.androidmapsextensions.Marker markerCity = mMap.addMarker(new MarkerOptions().position(new LatLng(cA.informations.address.latitude, cA.informations.address.longitude)));
            markerCity.setIcon(getBitmapDescriptor(R.drawable.ic_cluster));
            markerCity.setData(cA);

            //TODO Google anchor
            poiCityMarkers.add(markerCity);

            /*
            * Marker marker = myMap.addMarker(new MarkerOptions().position(new LatLng(geo1Dub,geo2Dub))); //...
markers.add(marker);
            * */
            /*
            * //Setto il marker
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
            *
            *
            * */

            Log.w("CITY",": "+cA.name);
            //Disegno i componenti grafici
            final com.androidmapsextensions.Marker finalMarkerCarCity = markerCity;
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    finalMarkerCarCity.setIcon(getBitmapDescriptor(makeBasicMarker(bitmap)));
                    //Aggiungo all'array
                    //poiCityMarkers.add(finalMarkerCarCity);
                    //mMapView.invalidate();
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

    private BitmapDescriptor getBitmapDescriptor(int icon){
        return BitmapDescriptorFactory.fromBitmap(getIconMarker(icon).getBitmap());
    }

    private BitmapDescriptor getBitmapDescriptor(Drawable drawable){
        return BitmapDescriptorFactory.fromBitmap(drawableToBitmap(drawable));
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              ButterKnife
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Quando l'utente preme il pulsante di chiusura del popup con il dettaglio di una macchina
    @OnClick(R.id.closePopupButton)
    public void onClosePopup() {

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
                        //TODO Google
                        /*if(!searchItemSelected)
                            initMapSearch();
                        else searchItemSelected = false;*/
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Mvp Methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void showCars(List<Car> carsList) {

    }

    @Override
    public void showFeeds(List<Feed> feedsList) {

    }

    @Override
    public void noCarsFound() {

    }

    @Override
    public void showSearchResult(List<SearchItem> searchItemList) {

    }

    @Override
    public void showBookingCar(Reservation reservation) {

    }

    @Override
    public void showConfirmDeletedCar() {

    }

    @Override
    public void showTripInfo(Car car, int timestamp_start) {

    }

    @Override
    public void setTripInfo(Trip trip) {

    }

    @Override
    public void removeTripInfo() {

    }

    @Override
    public void showReservationInfo(Car mCar, Reservation mReservation) {

    }

    @Override
    public void setReservationInfo(Car mCar, Reservation mReservation) {

    }

    @Override
    public void removeReservationInfo() {

    }

    @Override
    public void openTripEnd(int timestamp) {

    }

    @Override
    public void openNotification(int start, int end) {

    }

    @Override
    public void openReservationNotification() {

    }

    @Override
    public void openPreselectedCarPopup(Car car) {

    }

    @Override
    public void showCity(List<City> cityList) {
        drawCityMarkerOnMap(cityList);
    }

    @Override
    public void setFeedInters() {

    }

}
