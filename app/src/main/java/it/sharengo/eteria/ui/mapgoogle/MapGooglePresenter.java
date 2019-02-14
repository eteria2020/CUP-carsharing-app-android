package it.sharengo.eteria.ui.mapgoogle;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import it.sharengo.eteria.App;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.common.ErrorResponse;
import it.sharengo.eteria.data.models.Address;
import it.sharengo.eteria.data.models.Car;
import it.sharengo.eteria.data.models.City;
import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.data.models.GooglePlace;
import it.sharengo.eteria.data.models.Kml;
import it.sharengo.eteria.data.models.KmlServerPolygon;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.OsmPlace;
import it.sharengo.eteria.data.models.Post;
import it.sharengo.eteria.data.models.Reservation;
import it.sharengo.eteria.data.models.Response;
import it.sharengo.eteria.data.models.ResponseCar;
import it.sharengo.eteria.data.models.ResponseCity;
import it.sharengo.eteria.data.models.ResponseFeed;
import it.sharengo.eteria.data.models.ResponseGooglePlace;
import it.sharengo.eteria.data.models.ResponseGoogleRoutes;
import it.sharengo.eteria.data.models.ResponsePutReservation;
import it.sharengo.eteria.data.models.ResponseReservation;
import it.sharengo.eteria.data.models.ResponseSharengoMap;
import it.sharengo.eteria.data.models.ResponseTrip;
import it.sharengo.eteria.data.models.SearchItem;
import it.sharengo.eteria.data.models.User;
import it.sharengo.eteria.data.models.UserInfo;
import it.sharengo.eteria.data.repositories.AddressRepository;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.CarRepository;
import it.sharengo.eteria.data.repositories.CityRepository;
import it.sharengo.eteria.data.repositories.KmlRepository;
import it.sharengo.eteria.data.repositories.PostRepository;
import it.sharengo.eteria.data.repositories.PreferencesRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.map.BaseMapPresenter;
import it.sharengo.eteria.utils.NotificationFactory;
import it.sharengo.eteria.utils.schedulers.AndroidSchedulerProvider;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

import static android.content.Context.MODE_PRIVATE;

public class MapGooglePresenter extends BaseMapPresenter<MapGoogleMvpView> {

    private static final String TAG = MapGooglePresenter.class.getSimpleName();

    private Boolean mMapIsReady = false;
    private Boolean mLocationIsReady = false;
    private Double mLatitude;
    private Double mLongitude;

    public final AppRepository mAppRepository;
    private final PostRepository mPostRepository;
    private final CarRepository mCarRepository;
    private final AddressRepository mAddressRepository;
    private final PreferencesRepository mPreferencesRepository;
    private final UserRepository mUserRepository;
    private final CityRepository mCityRepository;
    private final KmlRepository mKmlRepository;

    /*
     *  REQUEST
     */
    private Observable<List<Post>> mPostsRequest;
    private Observable<Response> mCarsRequest;
    private Observable<ResponseCar> mCarsReservationRequest;
    private Observable<ResponseCar> mCarsInfoRequest;
    private Observable<ResponseCar> mCarsTripRequest;
    private Observable<Response> mPlatesRequest;
    private Observable<List<Car>> mFindPlatesRequest;
    private Observable<List<Address>> mFindAddressRequest;
    private Observable<ResponseGooglePlace> mFindPlacesRequest;
    private Observable<OsmPlace> mFindPlacesOsmRequest;
    private Observable<ResponseGoogleRoutes> mFindRoutesRequest;
    private Observable<List<SearchItem>> mFindSearchRequest;
    private Observable<ResponseTrip> mTripsRequest;
    private Observable<ResponseTrip> mTripsRequestLast;
    private Observable<ResponseReservation> mReservationsRequest;
    private Observable<ResponsePutReservation> mReservationRequest;
    private Observable<ResponseCity> mCityRequest;
    private Observable<ResponseFeed> mFeedRequest;
    private Observable<Kml> mKmlRequest;

    /*
     *  VAR
     */

    private Response mResponse;
    private ResponseCar mResponseReservationCar;
    private ResponseCar mResponseTripCar;
    private ResponseCar mResponseCarTrip;
    private ResponseTrip mResponseTrip;
    private ResponseTrip mResponseTripLast;
    private ResponseCar mResponseInfo;
    private ResponseReservation mResponseReservation;
    private Reservation mReservation;
    private List<Car> mPlates;
    public List<Car> mCachedPlates;
    private List<Address> mAddress;
    private List<SearchItem> mSearchItems;
    private List<SearchItem> historicItems;
    private ResponseGooglePlace mGooglePlace;
    private List<OsmPlace> mOsmPlace;
    private ResponseSharengoMap mSharengoMap;
    private ResponseGoogleRoutes mGoogleRoutes;
    private List<Post> mPosts;
    private boolean hideLoading;
    private boolean isPause;
    private boolean isTripExists;
    private boolean isTripOpening;
    private boolean isParked;
    private boolean isBookingExists;
    private boolean isBookingOpening;
    private int isTripOpeningCount;
    private int isBookingOpeningCount;
    private int timestamp_start;
    private List<City> mCitiesList;
    public boolean isFeeds;
    private List<Feed> mOffersList;
    private List<Feed> mEventsList;
    private long seconds;
    public float userLat, userLon;
    private int reservationTime;

    /*
     *  Timer
     */
    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();

    private Timer timer1min;
    private TimerTask timerTask1min;
    private final Handler handler1min = new Handler();

    private int timerInterval;
    private int INT_1_MIN = 60000;
    private int INT_5_SEC = 4000; //trasformato in 4 secondi per lentezza popup apertura corsa
    private int INT_30_SEC = 25000; //trasformato in 25 secondi per lentezza popup

    @Inject
    NotificationFactory notificationFactory;


    public MapGooglePresenter(SchedulerProvider schedulerProvider,
                              AppRepository appRepository,
                              PostRepository postRepository,
                              CarRepository carRepository,
                              AddressRepository addressRepository,
                              PreferencesRepository preferencesRepository,
                              UserRepository userRepository,
                              CityRepository cityRepository,
                              KmlRepository kmlRepository) {
        super(schedulerProvider,userRepository);
        mPostRepository = postRepository;
        mCarRepository = carRepository;
        mAddressRepository = addressRepository;
        mPreferencesRepository = preferencesRepository;
        mUserRepository = userRepository;
        mAppRepository = appRepository;
        mCityRepository = cityRepository;
        mKmlRepository = kmlRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.BOOKING);

        App.getInstance().getComponent().inject(this);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {
        startTimer();
    }


    @Override
    protected void subscribeRequestsOnResume() {
        getMvpView().setFeedInters();

//        notificationFactory.makeTestNotification();

        isTripExists = false;
        isParked = false;
        isBookingExists = false;
        timestamp_start = 0;
        seconds = 0;
        mReservationsRequest = null;
        mTripsRequest = null;
        mCarsReservationRequest = null;
        reservationTime = 0;

        //getMvpView().removeReservationInfo();
        //getMvpView().removeTripInfo();

        if(mUserRepository.getCachedUser() != null && !mUserRepository.getCachedUser().username.isEmpty()) {
            if(isPause){
                getMvpView().showLoading();
            }
            isPause = false;
            getReservations(false);
            getTrips(false);
        }

        startTimer();
    }

    @Override
    protected boolean showCustomLoading() {
        if(hideLoading)//TODO check if work prima era hideloading
            return true;
        else
            return super.showCustomLoading();
    }

   @Override
    public void attachView(MapGoogleMvpView mvpView, boolean recreation) {
        super.attachView(mvpView, recreation);
        checkReservationsResult();
    }

    /**
     * Create view when map is ready
     *
     */
    public void onMapIsReady() {
        mMapIsReady = true;
        viewCreated();
    }

    /**
     * Set variable with latitude and longitude of current position of user
     * and boolean with location ready set to true.
     *
     * @param  lat  latitude of current position of user.
     * @param  lng  longitude of current position of user.
     */
    public void onLocationIsReady(Double lat, Double lng) {
        mLocationIsReady = true;
        mLatitude = lat;
        mLongitude = lng;
    }

    void viewCreated() {

        isTripExists = false;
        isParked = false;
        isBookingExists = false;
        timestamp_start = 0;
        seconds = 0;

        loadPlates();
        if(mAppRepository.getIntentSelectedCar() != null && !mAppRepository.getIntentSelectedCar().isEmpty()) {
            String email= "";
          if (isAuth()){
             email=  mUserRepository.getCachedUser().username;
            }

            loadSelectedCars(mAppRepository.getIntentSelectedCar(), mAppRepository.getIntentSelectedCarCallingApp(),email);
            mAppRepository.setIntentSelectedCar(null);
            mAppRepository.setIntentSelectedCarCallingApp(null);
        }
        loadCarpopup();
        userLat = 0;
        userLon = 0;

        /*if(mUserRepository.getCachedUser() != null && !mUserRepository.getCachedUser().username.isEmpty())
            getReservations(false);*/

        //startTimer();
    }

    void viewDestroy() {
        stoptimertask();
    }

    void viewOnPause(){
        stoptimertask();
        mUserRepository.mCachedReservation = null;
        //hideLoading = false;
        isPause = true;
    }

    /**
     * Timer for checking reservation or active trips.
     */
    private void startTimer() {


        timer = new Timer();

        timerTask = new TimerTask() {
            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        loadPlates();
                    }
                });
            }
        };

        timer.schedule(timerTask, 60000, 60000); //300000


        setTimerReservertionTrip(INT_30_SEC);
    }

    private void setTimerReservertionTrip(int interval){
        Log.d("BOMB","setTimerReservertionTrip: value is " + interval);

        timerInterval = interval;

        if(timer1min != null) timer1min.cancel();

        timer1min = new Timer();

        timerTask1min = new TimerTask() {
            public void run() {

                handler1min.post(new Runnable() {
                    public void run() {

                        Log.d("BOMB","setTimerReservertionTrip: start TimerTask");

                        ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                        if(isConnected) {
                            if(mUserRepository.getCachedUser() != null && !mUserRepository.getCachedUser().username.isEmpty()) {
                                //if(seconds == 0 || ((System.currentTimeMillis() - seconds) / 1000) > 59){
                                getReservations(true); //Deve essere passato almeno un minuto dall'azione compiuta dall'utente (apertura porte o prenotazione)
                                 // }
                                getTrips(true);
                            }
                        }else{

                            //Timer
                            if(timerInterval != INT_5_SEC) setTimerReservertionTrip(INT_5_SEC);

                            if(getMvpView() != null) {
                                getMvpView().removeTripInfo();
                                getMvpView().removeReservationInfo();
                                reservationTime = 0;
                            }
                        }



                    }
                });
            }
        };

        timer1min.schedule(timerTask1min, 4000, interval);
    }

    /**
     * Stop timer if different from null.
     */
    private void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timer1min != null) {
            timer1min.cancel();
            timer1min = null;
        }
    }

    /**
     * Check if user is authenticated.
     *
     * @return      status of user authentication
     * @see         boolean
     */
    @Override
    public boolean isAuth(){
        if(mUserRepository.getCachedUser() != null && mUserRepository.getCachedUser().username != null && !mUserRepository.getCachedUser().username.isEmpty()) return true;
        return false;
    }



    private void loadCarpopup(){
        if(mCarRepository.getCarSelected() != null && isViewAttached())
            getMvpView().openPreselectedCarPopup(mCarRepository.getCarSelected());
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Esempio
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void loadPosts() {
        if(mPosts == null && mPostsRequest == null) {
            mPostsRequest = buildPostsRequest();
            addSubscription(mPostsRequest.unsafeSubscribe(getSubscriber()));
        }
    }

    private Observable<List<Post>> buildPostsRequest() {
        return mPostsRequest = mPostRepository.getPosts()
                .first()
                .compose(this.<List<Post>>handleDataRequest());
    }

    private Subscriber<List<Post>> getSubscriber(){
        return new Subscriber<List<Post>>() {
            @Override
            public void onCompleted() {
                mPostsRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mPostsRequest = null;
                getMvpView().showError(e);
            }

            @Override
            public void onNext(List<Post> postList) {
                mPosts = postList;

            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Load Cars
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Refresh cars (in view) available in map zone by latitude and longitude.
     *
     * @param  context   context of application.
     * @param  latitude  latitude of map's center.
     * @param  longitude longitude of map's center.
     * @param  user_lat  latitude of user.
     * @param  user_lon  longitude of user.
     * @param  radius    radius of map.
     * @param  bounds    bounds of map.
     * @param  refresh   update info from server.
     */
    public void refreshCars(Context context, float latitude, float longitude, float user_lat, float user_lon, int radius, LatLngBounds bounds, boolean refresh){
        hideLoading = true;

        if(refresh) { //Forzo il refresh
            loadCars(latitude, longitude, user_lat, user_lon, radius);
            if (isFeeds)
                loadFeeds(context, latitude, longitude, radius);
        }else{
            if(mCachedPlates != null){

                List<Car> carToShow = new ArrayList<>();

                for(Car carToCheck : mCachedPlates){

                    //LatLngBounds curScreen
                    if(carToCheck.latitude <= bounds.northeast.latitude && carToCheck.latitude >= bounds.southwest.latitude && carToCheck.longitude <= bounds.northeast.longitude && carToCheck.longitude >= bounds.southwest.longitude){
                        carToShow.add(carToCheck);
                    }
                }

                if(carToShow.isEmpty()){
                    getMvpView().noCarsFound();
                }else{
                    getMvpView().showCars(carToShow);
                }

            }else{

                loadCars(latitude, longitude, user_lat, user_lon, radius);
            }
        }
    }

    /**
     * Load cars from server available in map zone by latitude and longitude.
     *
     * @param  latitude  latitude of map's center.
     * @param  longitude longitude of map's center.
     * @param  user_lat  latitude of user.
     * @param  user_lon  longitude of user.
     * @param  radius    radius of map.
     */
    public void loadCars(float latitude, float longitude, float user_lat, float user_lon, int radius) {

        if( mCarsRequest == null) {

            mCarsRequest = null;
            mCarsRequest = buildCarsRequest(latitude, longitude, user_lat, user_lon, radius);
            addSubscription(mCarsRequest.unsafeSubscribe(getCarsSubscriber()));
        }
    }


    private Observable<Response> buildCarsRequest(float latitude, float longitude, float user_lat, float user_lon, int radius) {
        return mCarsRequest = mCarRepository.getCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, latitude, longitude, user_lat, user_lon, radius)
                .first()
                .compose(this.<Response>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkResult();
                    }
                });
    }

    private Subscriber<Response> getCarsSubscriber(){
        return new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                mCarsRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCarsRequest = null;
                try {
                    getMvpView().showError(e);
                    getMvpView().noCarsFound();
                }catch (NullPointerException c){}
            }

            @Override
            public void onNext(Response responseList) {
                mResponse = responseList;
            }
        };
    }

    private void checkResult(){
        if(mResponse.reason.isEmpty()){
            getMvpView().showCars(mResponse.data);
        }else{
            getMvpView().noCarsFound();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Load Kml
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load from server kml for determinate region on GMaps.
     *
     * @param  context  context of application.
     */
    public void loadKml(final Context context){
        Log.d("DIODO", "loadKml: start");

        SharedPreferences mPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);

        Type fooType = new TypeToken<List<KmlServerPolygon>>() {}.getType();

        Gson gson = new Gson();
        String json = mPref.getString(context.getString(R.string.preference_kml), "");
        List<KmlServerPolygon> obj = (ArrayList<KmlServerPolygon>) gson.fromJson(json, fooType);

        if(obj != null) getMvpView().showPolygon(obj);
        else {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, context.getString(R.string.endpointSite) + context.getString(R.string.routeZone), null, new com.android.volley.Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("DIODO", "loadKml: start");
                            parseKml(context, response);
                        }
                    }, new com.android.volley.Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("DIODO", "loadKml: error",error);
                        }
                    }) {
            };

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(jsObjRequest);
        }
    }

    private void parseKml(Context context, JSONObject response){

        Log.d("DIODO", "parseKml: " + response.toString());
        List<KmlServerPolygon> polygons = new ArrayList<>();

        Iterator<String> iter = response.keys();
        while (iter.hasNext()) {

            String key = iter.next();
            JSONObject item = null;
            try {
                //Provo a prendere un oggetto
                item = response.getJSONObject(key);
            } catch (JSONException e) {}


            if(item != null){

                JSONArray coordinates = null;
                try {
                    coordinates = new JSONObject(item.getString("areaUse")).getJSONArray("coordinates").getJSONArray(0);
                } catch (JSONException e) {}
                try {
                    coordinates = item.getJSONArray("coordinates").getJSONArray(0);
                } catch (JSONException e) {}

                if(coordinates != null){

                    List<LatLng> latLngs = new ArrayList<>();

                    for(int i = 0; i < coordinates.length(); i++){
                        try {
                            JSONArray coords = coordinates.getJSONArray(i);
                            latLngs.add(new LatLng(coords.getDouble(1),coords.getDouble(0)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(latLngs != null && latLngs.size() > 0) {
                        KmlServerPolygon poly = new KmlServerPolygon(latLngs);
                        polygons.add(poly);
                    }

                }
            }
        }

        SharedPreferences mPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPref.edit();
        Type fooType = new TypeToken<List<KmlServerPolygon>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(polygons, fooType);
        prefsEditor.putString(context.getString(R.string.preference_kml), json);
        prefsEditor.commit();

        try {
            getMvpView().showPolygon(polygons);
        }catch (NullPointerException e){}

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Load Offers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load feeds from server available in map zone by latitude and longitude.
     *
     * @param  context   context of application.
     * @param  latitude  latitude of map's center.
     * @param  longitude longitude of map's center.
     * @param  radius    radius of map.
     */
    public void loadFeeds(Context context, float latitude, float longitude, int radius){
        loadOffersList(context, latitude, longitude, radius);
    }

    private void loadOffersList(Context context, float latitude, float longitude, int radius) {

        hideLoading = true;

        if( mFeedRequest == null) {
            mFeedRequest = buildOffersRequest(context, latitude, longitude, radius);
            addSubscription(mFeedRequest.unsafeSubscribe(getOffersSubscriber()));
        }
    }

    private Observable<ResponseFeed> buildOffersRequest(final Context context, final float latitude, final float longitude, final int radius) {
        return mFeedRequest = mCityRepository.getOffersByCoordinates(context, latitude, longitude, radius)
                .first()
                .compose(this.<ResponseFeed>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        loadEventsList(context, latitude, longitude, radius);
                    }
                });
    }

    private Subscriber<ResponseFeed> getOffersSubscriber(){
        return new Subscriber<ResponseFeed>() {
            @Override
            public void onCompleted() {
                mFeedRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFeedRequest = null;
            }

            @Override
            public void onNext(ResponseFeed response) {
                mFeedRequest = null;
                mOffersList = response.data;
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              LOAD Events
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load events from server available in map zone by latitude and longitude.
     *
     * @param  context   context of application.
     * @param  latitude  latitude of map's center.
     * @param  longitude longitude of map's center.
     * @param  radius    radius of map.
     */
    public void loadEventsList(Context context, float latitude, float longitude, int radius) {

        hideLoading = true;

        if( mFeedRequest == null) {
            mFeedRequest = buildEventsRequest(context, latitude, longitude, radius);
            addSubscription(mFeedRequest.unsafeSubscribe(getEventsSubscriber()));
        }
    }

    private Observable<ResponseFeed> buildEventsRequest(Context context, float latitude, float longitude, int radius) {
        return mFeedRequest = mCityRepository.getEventsByCoordinates(context, latitude, longitude, radius)
                .first()
                .compose(this.<ResponseFeed>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        setAllFeedsList();
                    }
                });
    }

    private Subscriber<ResponseFeed> getEventsSubscriber(){
        return new Subscriber<ResponseFeed>() {
            @Override
            public void onCompleted() {
                mFeedRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFeedRequest = null;
            }

            @Override
            public void onNext(ResponseFeed response) {
                mEventsList = response.data;
            }
        };
    }

    private void setAllFeedsList(){

        List<Feed> feeds = new ArrayList<>();

        //Verifico se ci sono evento o offerte
        if(!mEventsList.isEmpty())
            feeds.addAll(mEventsList);

        for(Feed ff : mEventsList){

        }

        if(!mOffersList.isEmpty())
            feeds.addAll(mOffersList);

        for(Feed ff : mOffersList){

        }

        getMvpView().showFeeds(feeds);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Load Plates
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Load cars from server available in map. First, check if the car cache exists
     */
    public void loadPlatesCached() {
        hideLoading = true;

       /* if( mPlatesRequest == null) {
            mPlatesRequest = buildPlatesRequest();
            addSubscription(mPlatesRequest.unsafeSubscribe(getPlatesSubscriber()));
        }*/

        if(mCachedPlates != null){ if(getMvpView() != null) getMvpView().setNextCar(mCachedPlates); }
        else{
            loadPlates();
        }
    }

    /**
     * Load cars from server.
     */
    public void loadPlates() {
        hideLoading = true;

        if( mPlatesRequest == null) {
            mPlatesRequest = buildPlatesRequest();
            addSubscription(mPlatesRequest.unsafeSubscribe(getPlatesSubscriber()));
        }
    }

    private Observable<Response> buildPlatesRequest() {

        return mPlatesRequest = mCarRepository.getPlates(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, userLat, userLon)
                .first()
                .compose(this.<Response>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {

                        getMvpView().setNextCar(mCachedPlates);

                        if(mCachedPlates == null){
                            loadPlates();
                        }
                    }
                });
    }

    private Subscriber<Response> getPlatesSubscriber(){
        return new Subscriber<Response>() {
            @Override
            public void onCompleted() {
                mPlatesRequest = null;

            }

            @Override
            public void onError(Throwable e) {
                mPlatesRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(Response responseList) {
                if(responseList != null && responseList.reason.isEmpty() && responseList.data != null){
                    mCachedPlates = responseList.data;
                }

            }
        };
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Plates
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Find on server car by plate.
     *
     * @param  searchText  plate to search.
     */
    public void findPlates(String searchText) {
        hideLoading = true;

        if( mFindPlatesRequest == null) {
            mFindPlatesRequest = buildFindPlatesRequest(searchText);
            addSubscription(mFindPlatesRequest.unsafeSubscribe(getFindPlatesSubscriber()));
        }
    }


    private Observable<List<Car>> buildFindPlatesRequest(String searchText) {
        return mFindPlatesRequest = mCarRepository.findPlates(searchText)
                .first()
                .compose(this.<List<Car>>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkPlatesResult();
                    }
                });
    }

    private Subscriber<List<Car>> getFindPlatesSubscriber(){
        return new Subscriber<List<Car>>() {
            @Override
            public void onCompleted() {
                mFindPlatesRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFindPlatesRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(List<Car> carsList) {
                mPlates = carsList;
            }
        };
    }

    private void checkPlatesResult(){

        mSearchItems = new ArrayList<SearchItem>();

        for (Car carr : mPlates){
            mSearchItems.add(new SearchItem(carr.id, "plate", carr.longitude, carr.latitude));
        }

        getMvpView().showSearchResult(mSearchItems);

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Plate by ID
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Search car by ID and return to app the car's object.
     *
     * @param  plate  context of application.
     * @return        car object.
     * @see           Car
     */
    public Car findPlateByID(String plate) {
        Car carFind = null;

        if(mPlates != null){
            for(Car car : mPlates){
                if(car.id.toLowerCase().equals(plate.toLowerCase())) carFind = car;
            }
        }

        return carFind;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Car reservation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load from server car information (detail)
     *
     * @param  plate  plate for car reservation.
     */
    public void loadCarInfoPopup(String plate) {

        hideLoading = true;

        mCarsInfoRequest = null;
        mCarsInfoRequest = buildCarsInfoRequest(plate);
        addSubscription(mCarsInfoRequest.unsafeSubscribe(getCarsInfoSubscriber()));
    }


    private Observable<ResponseCar> buildCarsInfoRequest(String plate) {

        return mCarsInfoRequest = mCarRepository.getCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, plate,null, null, null,null)
                .first()
                .compose(this.<ResponseCar>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkCarInfoResult();
                    }
                });
    }

    private Subscriber<ResponseCar> getCarsInfoSubscriber(){
        return new Subscriber<ResponseCar>() {
            @Override
            public void onCompleted() {
                mCarsInfoRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCarsInfoRequest = null;

            }

            @Override
            public void onNext(ResponseCar responseList) {
                mResponseInfo = responseList;
            }
        };
    }

    private void checkCarInfoResult(){

        if(mResponseInfo.reason.isEmpty() && mResponseInfo.data != null){
            getMvpView().onLoadCarInfo(mResponseInfo.data);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Car Intent
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load from server car information (detail)
     *
     * @param  plate  plate for car reservation.
     */
    public void loadSelectedCars(String plate, String callingApp,String email) {

        hideLoading = true;
        //Log.d("BOMB", "loadSelectedCars " + plate +user_lat + user_lon + callingApp);

        mCarsInfoRequest = null;
        mCarsInfoRequest = buildCarSelectedRequest(plate, callingApp,email);
        addSubscription(mCarsInfoRequest.unsafeSubscribe(getCarSelectedSubscriber()));
    }


    private Observable<ResponseCar> buildCarSelectedRequest(String plate, String callingApp,String email) {

        return mCarsInfoRequest = Observable.just(1).delay(3, TimeUnit.SECONDS).concatMap(i -> mCarRepository.getCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, plate, String.valueOf(userLat), String.valueOf( userLon), callingApp,email))
                .first()
                .compose(this.<ResponseCar>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkCarSelectedResult();
                    }
                });
    }

    private Subscriber<ResponseCar> getCarSelectedSubscriber(){
        return new Subscriber<ResponseCar>() {
            @Override
            public void onCompleted() {
                mCarsInfoRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCarsInfoRequest = null;

            }

            @Override
            public void onNext(ResponseCar responseList) {
                getMvpView().setSelectedCar(responseList.data);
            }
        };
    }

    private void checkCarSelectedResult(){

        if(mResponseInfo.reason.isEmpty() && mResponseInfo.data != null){
            getMvpView().onLoadCarInfo(mResponseInfo.data);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Car Details
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load from server car information (detail)
     *
     * @param  plate  plate for car reservation.
     */
    public void loadCarDetails(String plate, String callingApp) {

        hideLoading = true;
        //Log.d("BOMB", "loadSelectedCars " + plate +user_lat + user_lon + callingApp);

        mCarsInfoRequest = null;
        mCarsInfoRequest = buildCarDetailsRequest(plate);
        addSubscription(mCarsInfoRequest.unsafeSubscribe(getCarDetailsSubscriber()));
    }


    public Observable<ResponseCar> buildCarDetailsRequest(String plate) {

        return mCarRepository.getCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, plate,null, null, null, null)
                .first()
                .compose(this.<ResponseCar>handleDataRequest());
    }

    private Subscriber<ResponseCar> getCarDetailsSubscriber(){
        return new Subscriber<ResponseCar>() {
            @Override
            public void onCompleted() {
                mCarsInfoRequest = null;
                checkCarDetailsResult();
            }

            @Override
            public void onError(Throwable e) {
                mCarsInfoRequest = null;

            }

            @Override
            public void onNext(ResponseCar responseList) {
                getMvpView().setSelectedCar(responseList.data);
            }
        };
    }

    private void checkCarDetailsResult(){

        if(mResponseInfo.reason.isEmpty() && mResponseInfo.data != null){
            getMvpView().onLoadCarInfo(mResponseInfo.data);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Address Sharengo
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Find place with Sharengo Nominatim API by text search by user.
     *
     * @param  context  context of application.
     * @param  searchText  text serch by user.
     * @param  location  user location. Format: lan,long.
     * @param  lang  language
     */
//    public void searchPlaceSharengo(Context context, String searchText, Location location, String lang) {
//        hideLoading = true;
//
//        if( mFindPlacesRequest == null) {
//            mFindPlacesRequest = buildFindPlacesSharengoRequest(context, searchText, location, lang);
//            addSubscription(mFindPlacesRequest.unsafeSubscribe(getFindPlacesSubscriber()));
//        }
//    }
//
//
//    private Observable<ResponseGooglePlace> buildFindPlacesSharengoRequest(Context context, String searchText, Location location, String lang) {
//        return mFindPlacesRequest = mAddressRepository.searchPlace(searchText, location.getLatitude()+", "+location.getLongitude(), lang, context.getString(R.string.google_place_api_key))
//                .first()
//                .compose(this.<ResponseGooglePlace>handleDataRequest())
//                .doOnCompleted(new Action0() {
//                    @Override
//                    public void call() {
//                        checkPlacesSharengoResult();
//                    }
//                });
//    }
//
//    private Subscriber<ResponseSharengoMap> getFindPlacesSharengoSubscriber(){
//        return new Subscriber<ResponseSharengoMap>() {
//            @Override
//            public void onCompleted() {
//                mFindPlacesRequest = null;
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                mFindPlacesRequest = null;
//                //getMvpView().showError(e);
//            }
//
//            @Override
//            public void onNext(ResponseSharengoMap addressList) {
//                //mAddress = addressList; TODO
//                mSharengoMap = addressList;
//            }
//        };
//    }
//
//    private void checkPlacesSharengoResult(){
//
//        /*mSearchItems = new ArrayList<SearchItem>();
//
//        for (Address address : mAddress){
//
//            String type = "address";
//
//            if(historicItems != null)
//                for (SearchItem sI : historicItems) {
//                    if (address.display_name.equals(sI.display_name)) type = "historic";
//                }
//
//            mSearchItems.add(new SearchItem(address.display_name, type, address.longitude, address.latitude));
//        }
//
//        getMvpView().showSearchResult(mSearchItems);*/
//
//        mSearchItems = new ArrayList<SearchItem>();
//
//        if(mGooglePlace != null && mGooglePlace.results != null){
//
//            for(int i = 0; i < mGooglePlace.results.size(); i++){
//
//                GooglePlace gPlace = mGooglePlace.results.get(i);
//                String type = "address";
//
//                if(historicItems != null)
//                    for (SearchItem sI : historicItems) {
//                        if (gPlace.address.equals(sI.display_name)) type = "historic";
//                    }
//
//                mSearchItems.add(new SearchItem(gPlace.address, type, gPlace.geometry.location.longitude, gPlace.geometry.location.latitude));
//            }
//        }
//
//        getMvpView().showSearchResult(mSearchItems);
//
//    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Address
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Find place with Google Place API by text search by user.
     *
     * @param  context  context of application.
     * @param  searchText  text serch by user.
     * @param  location  user location. Format: lan,long.
     * @param  lang  language
     */
    public void searchPlace(Context context, String searchText, Location location, String lang) {
        hideLoading = true;

        if( mFindPlacesRequest == null) {
            mFindPlacesRequest = buildFindPlacesRequest(context, searchText, location, lang);
            addSubscription(mFindPlacesRequest.unsafeSubscribe(getFindPlacesSubscriber()));
        }
    }


    private Observable<ResponseGooglePlace> buildFindPlacesRequest(Context context, String searchText, Location location, String lang) {
        return mFindPlacesRequest = mAddressRepository.searchPlace(searchText, location.getLatitude()+", "+location.getLongitude(), lang, context.getString(R.string.google_place_api_key))
                .first()
                .compose(this.<ResponseGooglePlace>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkPlacesResult();
                    }
                });
    }

    private Subscriber<ResponseGooglePlace> getFindPlacesSubscriber(){
        return new Subscriber<ResponseGooglePlace>() {
            @Override
            public void onCompleted() {
                mFindPlacesRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFindPlacesRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(ResponseGooglePlace addressList) {
                //mAddress = addressList; TODO
                mGooglePlace = addressList;
            }
        };
    }

    private void checkPlacesResult(){

        /*mSearchItems = new ArrayList<SearchItem>();

        for (Address address : mAddress){

            String type = "address";

            if(historicItems != null)
                for (SearchItem sI : historicItems) {
                    if (address.display_name.equals(sI.display_name)) type = "historic";
                }

            mSearchItems.add(new SearchItem(address.display_name, type, address.longitude, address.latitude));
        }

        getMvpView().showSearchResult(mSearchItems);*/

        mSearchItems = new ArrayList<SearchItem>();

        if(mGooglePlace != null && mGooglePlace.results != null){

            for(int i = 0; i < mGooglePlace.results.size(); i++){

                GooglePlace gPlace = mGooglePlace.results.get(i);
                String type = "address";

                if(historicItems != null)
                    for (SearchItem sI : historicItems) {
                        if (gPlace.address.equals(sI.display_name)) type = "historic";
                    }

                mSearchItems.add(new SearchItem(gPlace.address, type, gPlace.geometry.location.longitude, gPlace.geometry.location.latitude));
            }
        }

        getMvpView().showSearchResult(mSearchItems);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Address by OSM
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Find place with Google Place API by text search by user.
     *
     * @param  context  context of application.
     * @param  searchText  text serch by user.
     * @param  format  xml.
     * @param  polygon  polygon
     * @param  addressdetails  addressdetails
     */
    public void searchPlaceOsm(Context context, String searchText, String format, String polygon, String addressdetails,String countrycode,String dedupe) {
        hideLoading = true;

        if( mFindPlacesOsmRequest == null) {
            mFindPlacesOsmRequest = buildFindPlacesOsmRequest(context, searchText,format,polygon,addressdetails,countrycode,dedupe);
            addSubscription(mFindPlacesOsmRequest.unsafeSubscribe(getFindPlacesOsmSubscriber()));
        }
    }


    private Observable<OsmPlace> buildFindPlacesOsmRequest(Context context, String searchText, String format, String polygon, String addressdetails,String countrycode,String dedupe) {
        return mFindPlacesOsmRequest = mAddressRepository.searchPlaceOsm(searchText,format,polygon,addressdetails,countrycode,dedupe)
                .take(5)
                .doOnSubscribe(() -> {mOsmPlace = new ArrayList<>();
                Log.d("BOMB" , "inizio find place request");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Log.d("BOMB" , "fine find place request");
                        checkPlacesOsmResult();

                    }
                });
    }

    private Subscriber<OsmPlace> getFindPlacesOsmSubscriber(){
        return new Subscriber<OsmPlace>() {

            @Override
            public void onCompleted() {
                mFindPlacesOsmRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFindPlacesOsmRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(OsmPlace addressList) {
                //mAddress = addressList; TODO
                mOsmPlace.add(addressList);
                Log.d("BOMB" , "onNext find place request");
            }
        };
    }

    private void checkPlacesOsmResult(){

        /*mSearchItems = new ArrayList<SearchItem>();

        for (Address address : mAddress){

            String type = "address";

            if(historicItems != null)
                for (SearchItem sI : historicItems) {
                    if (address.display_name.equals(sI.display_name)) type = "historic";
                }

            mSearchItems.add(new SearchItem(address.display_name, type, address.longitude, address.latitude));
        }

        getMvpView().showSearchResult(mSearchItems);*/

        mSearchItems = new ArrayList<SearchItem>();

        if(mOsmPlace != null){

            for(OsmPlace place: mOsmPlace){

                String type = "address";

              /* if(historicItems != null)
                    for (SearchItem sI : historicItems) {
                        if (gPlace.address.equals(sI.display_name)) type = "historic";
                    }*/

                mSearchItems.add(new SearchItem(place.getDisplayName(), type, Float.parseFloat(place.getLon()),Float.parseFloat(place.getLat())));
            }
        }

        getMvpView().showSearchResult(mSearchItems);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Search Item
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve from server element search by user.
     *
     * @param  searchText text to search.
     * @param  context    context of application.
     * @param  mPrefs     preference of app.
     */
    public void getSearchItems(String searchText, Context context, SharedPreferences mPrefs) {
        hideLoading = true;

        if( mFindSearchRequest == null) {
            mFindSearchRequest = buildFindSearchRequest(searchText, context, mPrefs);
            addSubscription(mFindSearchRequest.unsafeSubscribe(getFindSearchSubscriber(context, mPrefs)));
        }
    }

    private Observable<List<SearchItem>> buildFindSearchRequest(String searchText, final Context context, SharedPreferences mPrefs) {


        return mFindSearchRequest = mPreferencesRepository.getHistoricSearch(searchText, mPrefs, null)
                .first()
                .compose(this.<List<SearchItem>>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkSearchResult();
                    }
                });
    }

    private Subscriber<List<SearchItem>> getFindSearchSubscriber(final Context context, final SharedPreferences mPrefs){
        return new Subscriber<List<SearchItem>>() {
            @Override
            public void onCompleted() {
                mFindSearchRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFindSearchRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(List<SearchItem> searchItemList) {

                historicItems = searchItemList;

                if(historicItems.isEmpty())
                    historicItems.add(new SearchItem(context.getString(R.string.search_favoriteempty_label), "none"));

                Collections.reverse(historicItems);

                historicItems = historicItems.subList(0, Math.min(historicItems.size(), 15));

            }
        };
    }

    private void checkSearchResult(){
        getMvpView().showSearchResult(historicItems);
    }

    /**
     * Save historic of search value.
     *
     * @param  mPref       preference of app.
     * @param  searchItem  item to search.
     */
    public void saveSearchResultOnHistoric(SharedPreferences mPref, SearchItem searchItem){
        mPreferencesRepository.saveSearchResultOnHistoric(mPref, searchItem);
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              User
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Return the user's object to app.
     *
     * @return   user object.
     * @see      User
     */
    public User getUser(){
        return mUserRepository.getCachedUser();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Booking car
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Save on server booking information for car selected.
     *
     * @param  car       car for retrieve booking information.
     * @param  user_lat  latitude of user.
     * @param  user_lon  longitude of user.
     * @param  context   context of application.
     */
    public void bookingCar(Car car, float user_lat, float user_lon, Context context){
        hideLoading = true;

        if(getMvpView()!=null)
        getMvpView().showHCustomLoading();

        seconds = System.currentTimeMillis();

        if(car != null) {
            if (mReservationRequest == null) {
                mReservationRequest = buildReservationRequest(car, user_lat, user_lon);
                addSubscription(mReservationRequest.unsafeSubscribe(getReservationSubscriber(context)));
            }
        }
    }

    private Observable<ResponsePutReservation> buildReservationRequest(Car car, float user_lat, float user_lon) {
        return mReservationRequest = mUserRepository.postReservations(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, car, user_lat, user_lon)
                .first()
                .compose(this.<ResponsePutReservation>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if(mReservation != null)

                            getReservations(true,500);
                    }
                });
    }

    private Subscriber<ResponsePutReservation> getReservationSubscriber(final Context context){
        return new Subscriber<ResponsePutReservation>() {
            @Override
            public void onCompleted() {
                mReservationRequest = null;
                //getMvpView().hideCustomLoading();
            }

            @Override
            public void onError(Throwable e) {
                mReservationRequest = null;
                //getMvpView().showError(e);
                getMvpView().hideHCustomLoading();
            }

            @Override
            public void onNext(ResponsePutReservation response) {

                if(!response.reason.isEmpty() && response.reason.equals("Reservation created successfully")) {
                    mReservation = response.reservation;
                    isBookingOpening=true;
                    isBookingOpeningCount = 0;
                }
                else{
                    switch (response.splitMessages()){

                        case noError:
                            break;
                        case generic:
                            mReservation=null;
                            getMvpView().generalError();
                            break;
                        case status:
                            mReservation=null;
                            getMvpView().carBusyError();
                            break;
                        case reservation:
                            mReservation=null;
                            getMvpView().tooManyReservationError();
                            break;
                        case limit:
                            mReservation=null;
                            getMvpView().carAlreadyBooked();
                            break;
                        case trip:
                            mReservation=null;
                            getMvpView().reserveOnTripError();
                            break;
                        case unauthorized:
                            mReservation=null;
                            getMvpView().unauthorizedError();
                            break;

                    }
                }

//                    if(!response.reason.isEmpty() && response.reason.equals("Error: reservation:true - status:false - trip:false - limit:false - limit_archive:false")) {
//                    getMvpView().generalError();
//                }else{
//                    mReservation = null;
//                    getMvpView().carAlreadyBooked();
//                }
            }
        };
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Delete booking
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Delete on server booking information for car selected.
     *
     * @param  id  id of booking car.
     */
    public void deleteBookingCar(int id){

        hideLoading = true;

        getMvpView().showHCustomLoading();

        isBookingExists = false;
        reservationTime = 0;

        if( mReservationRequest == null) {
            mReservationRequest = buildDeleteReservationRequest(id);
            addSubscription(mReservationRequest.unsafeSubscribe(getDeleteReservationSubscriber()));
        }
    }

    private Observable<ResponsePutReservation> buildDeleteReservationRequest(int id) {
        return mReservationRequest = mUserRepository.deleteReservations(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, id, userLat, userLon)
                .first()
                .compose(this.<ResponsePutReservation>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getMvpView().hideHCustomLoading();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(isViewAttached()){
                                    getMvpView().showConfirmDeletedCar();
                                    getReservations(true);
                                }

                            }
                        }, 2500);

                    }
                });
    }

    private Subscriber<ResponsePutReservation> getDeleteReservationSubscriber(){
        return new Subscriber<ResponsePutReservation>() {
            @Override
            public void onCompleted() {
                mReservationRequest = null;
                //getMvpView().hideCustomLoading();
            }

            @Override
            public void onError(Throwable e) {
                mReservationRequest = null;
                getMvpView().hideHCustomLoading();
            }

            @Override
            public void onNext(ResponsePutReservation response) {
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Open door
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * controllo reservation e dettagi macchina per notificare lk'utente in caso di bonus o non
     *
     * @param  car     car to open.
     */
    public void checkOpenDoor(final Car car) {

        getMvpView().showHCustomLoading();
        Car carPref = mPreferencesRepository.getReservationCar();

        if(carPref!=null &&  car.id.equalsIgnoreCase(carPref.id) && carPref.getValidBonus().size()>0 ){
            if(mPreferencesRepository.getReservationTimestamp()>=System.currentTimeMillis()/1000) {
                int bonusValue = carPref.getValidBonus().get(0).getValue();
                getMvpView().openDoorConfirm(car, bonusValue);
            }
        }else {
            addSubscription(buildCarDetailsRequest(car.id).concatMap(responseCar -> Observable.just(responseCar.data)).subscribe(car1 -> {
                        if(car1!=null && car1.getValidBonus().size()>0){
                            int bonusValue = car1.getValidBonus().get(0).getValue();
                            getMvpView().openDoorConfirm(car1, bonusValue);
                        }else {
                            getMvpView().openDoorConfirm(car1, 0);
                        }
            }));
        }

    }


    /**
     * Send command for open door of car.
     *
     * @param  car     car to open.
     * @param  action  action to execute.
     */
    public void openDoor(Car car, String action) {

        getMvpView().showHCustomLoading();

        seconds = System.currentTimeMillis();

        isBookingExists = false;
        if(action.equals("unpark")) isParked = true;

        if( mCarsTripRequest == null) {

            mCarsTripRequest = null;
            mCarsTripRequest = buildCarsOpenRequest(car, action, userLat, userLon);
            addSubscription(mCarsTripRequest.unsafeSubscribe(getCarsOpenSubscriber()));
        }
    }


    private Observable<ResponseCar> buildCarsOpenRequest(final Car car, final String action, float user_lat, float user_lon) {

        return mCarsTripRequest = mCarRepository.openCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, car.id, action, user_lat, user_lon)
                .first()
                .compose(this.<ResponseCar>handleDataRequest())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("BOMB","some error occurs",throwable);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if(mResponseCarTrip.status.equalsIgnoreCase("200")) {
                            getTrips(true,5000);
                        }
//                       if(timestamp_start == 0) timestamp_start = (int) (System.currentTimeMillis() / 1000L);
                        //loadCarsTrip(car.id);






                    }
                });
    }

    private Subscriber<ResponseCar> getCarsOpenSubscriber(){
        return new Subscriber<ResponseCar>() {
            @Override
            public void onCompleted() {
                mCarsTripRequest = null;
                //getMvpView().hideCustomLoading();
            }

            @Override
            public void onError(Throwable e) {
                mCarsTripRequest = null;
                if(e instanceof ErrorResponse) {
                    try {
                        //noinspection ConstantConditions
                        switch (ResponseCar.splitMessages(((ErrorResponse) e).rawMessage)) {
                            case noError:
                                break;
                            case generic:
                                mReservation = null;
                                getMvpView().generalError();
                                break;
                            case status:
                                mReservation = null;
                                getMvpView().carBusyError();
                                break;
                            case reservation:
                                mReservation = null;
                                getMvpView().tooManyReservationError();
                                break;
                            case trip:
                                mReservation = null;
                                getMvpView().reserveOnTripError();
                                break;

                        }
                    } catch (Exception ex) {
                        Log.e("BOMB", "exception?", ex);
                    }
                }
                //getMvpView().showError(e);
                getMvpView().hideHCustomLoading();
            }

            @Override
            public void onNext(ResponseCar responseList) {
                mResponseCarTrip = responseList;

                if(mResponseCarTrip!=null){
                    if(mResponseCarTrip.status.equalsIgnoreCase("200")){
                        if(!isParked)
                            getMvpView().openCarNotification();
                        isTripOpening=true;
                        isTripOpeningCount=0;

                    }else if(mResponseCarTrip.status.equalsIgnoreCase("400")){
                        switch (mResponseCarTrip.splitMessages()){
                            case noError:
                                break;
                            case generic:
                                mReservation=null;
                                getMvpView().generalError();
                                break;
                            case status:
                                mReservation=null;
                                getMvpView().carBusyError();
                                break;
                            case reservation:
                                mReservation=null;
                                getMvpView().tooManyReservationError();
                                break;
                            case trip:
                                mReservation=null;
                                getMvpView().reserveOnTripError();
                                break;

                        }
                    }

                }
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Close door
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Send command for open door of car.
     *
     * @param  car     car to open.
     * @param  action  action to execute.
     */
    public void closeCar(String carPlate, String action) {

        //getMvpView().showHCustomLoading();

        seconds = System.currentTimeMillis();

        isBookingExists = false;


        if( mCarsTripRequest == null) {

            mCarsTripRequest = null;
            mCarsTripRequest = buildCarsCloseRequest(carPlate, action, userLat, userLon);
            addSubscription(mCarsTripRequest.unsafeSubscribe(getCarsCloseSubscriber()));
        }
    }


    private Observable<ResponseCar> buildCarsCloseRequest(final String carPlate, final String action, float user_lat, float user_lon) {

        return mCarsTripRequest = mCarRepository.closeCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, carPlate, action, user_lat, user_lon)
                .first()
                .compose(this.<ResponseCar>handleDataRequest())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("BOMB","some error occurs",throwable);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if(mResponseCarTrip.status.equalsIgnoreCase("200")) {
                            if(isViewAttached()) {
                                getMvpView().showPopupAfterButtonClosePressed();
                            }
                        }
//                       if(timestamp_start == 0) timestamp_start = (int) (System.currentTimeMillis() / 1000L);
                        //loadCarsTrip(car.id);

                    }
                });
    }

    private Subscriber<ResponseCar> getCarsCloseSubscriber(){
        return new Subscriber<ResponseCar>() {
            @Override
            public void onCompleted() {
                mCarsTripRequest = null;
                //getMvpView().hideCustomLoading();
            }

            @Override
            public void onError(Throwable e) {
                mCarsTripRequest = null;
                if(e instanceof ErrorResponse) {
                    try {
                        //noinspection ConstantConditions
                        switch (ResponseCar.splitMessages(((ErrorResponse) e).rawMessage)) {
                            case noError:
                                break;
                            case generic:
                                mReservation = null;
                                getMvpView().generalError();
                                break;
                            case status:
                                mReservation = null;
                                getMvpView().carBusyError();
                                break;
                            case reservation:
                                mReservation = null;
                                getMvpView().tooManyReservationError();
                                break;
                            case trip:
                                mReservation = null;
                                getMvpView().reserveOnTripError();
                                break;

                        }
                    } catch (Exception ex) {
                        Log.e("BOMB", "exception?", ex);
                    }
                }
                //getMvpView().showError(e);
                getMvpView().hideHCustomLoading();
            }

            @Override
            public void onNext(ResponseCar responseList) {
                mResponseCarTrip = responseList;

                if(mResponseCarTrip!=null){
                    if(mResponseCarTrip.status.equalsIgnoreCase("200")){
                       
                        isTripOpening=false;
                        isTripOpeningCount=0;

                    }else if(mResponseCarTrip.status.equalsIgnoreCase("400")){
                        switch (mResponseCarTrip.splitMessages()){
                            case noError:
                                break;
                            case generic:
                                mReservation=null;
                                getMvpView().generalError();
                                break;
                            case status:
                                mReservation=null;
                                getMvpView().carBusyError();
                                break;
                            case reservation:
                                mReservation=null;
                                getMvpView().tooManyReservationError();
                                break;
                            case trip:
                                mReservation=null;
                                getMvpView().reserveOnTripError();
                                break;

                        }
                    }

                }
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Trips
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void getTrips(boolean refreshInfo){

        hideLoading = true;

        if( mTripsRequest == null) {
            mTripsRequest = buildTripsRequest(refreshInfo);
            addSubscription(mTripsRequest.unsafeSubscribe(getTripsSubscriber()));
        }
    }
    void getTrips(boolean refreshInfo, long delay){

        hideLoading = true;

        if( mTripsRequest == null) {
            mTripsRequest = buildTripsRequest(refreshInfo,delay);
            addSubscription(mTripsRequest.unsafeSubscribe(getTripsSubscriber()));
        }
    }

    private Observable<ResponseTrip> buildTripsRequest(boolean refreshInfo) {
        return mTripsRequest = mUserRepository.getCurrentTrips(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password)
                .first()
                .compose(this.<ResponseTrip>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkTripsResult();
                    }
                });
    }

    private Observable<ResponseTrip> buildTripsRequest(boolean refreshInfo, long delay) {
        return mTripsRequest = mUserRepository.getCurrentTrips(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password)
                .first()
                .delaySubscription(delay,TimeUnit.MILLISECONDS)
                .compose(this.<ResponseTrip>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkTripsResult();
                    }
                });
    }

    private Subscriber<ResponseTrip> getTripsSubscriber(){
        return new Subscriber<ResponseTrip>() {
            @Override
            public void onCompleted() {
                mTripsRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mTripsRequest = null;
            }

            @Override
            public void onNext(ResponseTrip response) {
                mResponseTrip = response;
            }
        };
    }

    private void checkTripsResult(){

        Log.d("BOMB","checkTripsResult: start " + mResponseTrip.reason);

        if(mResponseTrip.reason.isEmpty() && mResponseTrip.trips != null && mResponseTrip.trips.size() > 0){
            timestamp_start = mResponseTrip.trips.get(0).timestamp_start;
            isTripExists = true;
            isTripOpening = false;

            loadCarsTrip(mResponseTrip.trips.get(0).plate);

            //Timer
            if(timerInterval != INT_30_SEC) setTimerReservertionTrip(INT_30_SEC);
        }else{

            Log.d("BOMB","checkTripsResult: check trip open  " + isTripOpening + " " + !isTripExists);
            if(isTripOpening && !isTripExists){
                if(isTripOpeningCount++<10) {
                    if (timerInterval != INT_5_SEC) setTimerReservertionTrip(INT_5_SEC);
                }else {
                    isTripOpening=false;
                    if (timerInterval != INT_30_SEC) setTimerReservertionTrip(INT_30_SEC);
                }
            }

            getMvpView().hideLoading();

            if(isTripExists){
                isTripExists = false;

                loadPlates();
                //controllo della chiusura corsa
                showNotificationOnLastTrip();

                timestamp_start = 0;
                //timerTask1min.cancel();
                //timer.cancel();


            }

            if(!isTripOpening && !isBookingExists && !isBookingOpening) {
                getMvpView().removeTripInfo();

                //Timer
                if (timerInterval != INT_30_SEC) setTimerReservertionTrip(INT_30_SEC);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Reservations
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void getReservations(boolean refreshInfo){
        Log.w("isPause","getReservations: "+mReservationsRequest);
        if( mReservationsRequest == null) {
            mReservationsRequest = buildReservationsRequest(refreshInfo);
            addSubscription(mReservationsRequest.unsafeSubscribe(getReservationsSubscriber()));
        }
    }

    void getReservations(boolean refreshInfo, long delay){
        Log.w("isPause","getReservations: "+mReservationsRequest);
        if( mReservationsRequest == null) {
            mReservationsRequest = buildReservationsRequest(refreshInfo, delay);
            addSubscription(mReservationsRequest.unsafeSubscribe(getReservationsSubscriber()));
        }
    }

    private Observable<ResponseReservation> buildReservationsRequest(boolean refreshInfo) {
        return mReservationsRequest = mUserRepository.getReservations(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, refreshInfo)
                .first()
                .compose(this.<ResponseReservation>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkReservationsResult();
                    }
                });
    }

    private Observable<ResponseReservation> buildReservationsRequest(boolean refreshInfo,long delay) {
        return mReservationsRequest = mUserRepository.getReservations(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, refreshInfo)
                .first()
                .delaySubscription(delay,TimeUnit.MILLISECONDS)
                .compose(this.<ResponseReservation>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkReservationsResult();
                    }
                });
    }

    private Subscriber<ResponseReservation> getReservationsSubscriber(){
        return new Subscriber<ResponseReservation>() {
            @Override
            public void onCompleted() {
                mReservationsRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mReservationsRequest = null;
                if(getMvpView()!=null)
                    getMvpView().hideHCustomLoading();
            }

            @Override
            public void onNext(ResponseReservation response) {
                mResponseReservation = response;
            }
        };
    }

    private void checkReservationsResult(){

        if(mResponseReservation == null )
            //Non deve fare il checkreservation modifica per resume da background
            return;
        if( mResponseReservation.reason.isEmpty() && mResponseReservation.reservations != null && mResponseReservation.reservations.size() > 0){

            //Verifico che non sia scaduta
            long unixTime = System.currentTimeMillis() / 1000L;
            int diffTime = (int) (unixTime - mResponseReservation.reservations.get(0).timestamp_start);

            reservationTime = (mResponseReservation.reservations.get(0).length - diffTime) * 1000;

            if(reservationTime > 0) {
                loadCarsReservation(mResponseReservation.reservations.get(0).car_plate);
                isBookingExists = true;
                isBookingOpening = false;

                //Timer
                if(timerInterval != INT_30_SEC) setTimerReservertionTrip(INT_30_SEC);
            }else{
                //getTrips(true);
            }
        }else{
            //getMvpView().removeReservationInfo();
            if(isBookingOpening && !isBookingExists){
                if(isBookingOpeningCount++<10) {
                    if (timerInterval != INT_5_SEC) setTimerReservertionTrip(INT_5_SEC);
                }else {
                    isBookingOpening=false;
                    if (timerInterval != INT_30_SEC) setTimerReservertionTrip(INT_30_SEC);
                }
            }
            if(isBookingExists && reservationTime > 0){
                isBookingExists = false;
                getMvpView().openReservationNotification();
            }

            if(!isBookingOpening && !isTripExists && !isTripOpening) {
                getMvpView().removeReservationInfo();

                //Timer
                if (timerInterval != INT_30_SEC) setTimerReservertionTrip(INT_30_SEC);
            }
            //getTrips(true);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Car reservation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load from server car reservation by plate.
     *
     * @param  plate  plate for car reservation.
     */
    public void loadCarsReservation(String plate) {

        if( mCarsReservationRequest == null) {

            mCarsReservationRequest = null;
            mCarsReservationRequest = buildCarsReservationRequest(plate);
            addSubscription(mCarsReservationRequest.unsafeSubscribe(getCarsReservationSubscriber()));
        }
    }


    private Observable<ResponseCar> buildCarsReservationRequest(String plate) {

        return mCarsReservationRequest = mCarRepository.getCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, plate, null, null, null,null)
                .first()
                .compose(this.<ResponseCar>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkCarReservationResult();
                    }
                });
    }

    private Subscriber<ResponseCar> getCarsReservationSubscriber(){
        return new Subscriber<ResponseCar>() {
            @Override
            public void onCompleted() {
                mCarsReservationRequest = null;
            }

            @Override
            public void onError(Throwable e) {

                mCarsReservationRequest = null;
                //getMvpView().showError(e);
                if(isViewAttached())
                    getMvpView().hideHCustomLoading();
            }

            @Override
            public void onNext(ResponseCar responseList) {
                mResponseReservationCar = responseList;
            }
        };
    }

    private void checkCarReservationResult(){
        if(mResponseReservationCar.reason.isEmpty() && mResponseReservationCar.data != null){

            //mResponseReservation.reservations.get(0).length = 700;


            long unixTime = System.currentTimeMillis() / 1000L;
            int diffTime = (int) (unixTime - mResponseReservation.reservations.get(0).timestamp_start);

            reservationTime = (mResponseReservation.reservations.get(0).length - diffTime) * 1000;

            if(reservationTime > 0) {
                getMvpView().showReservationInfo(mResponseReservationCar.data, mResponseReservation.reservations.get(0));
                //getMvpView().hideLoading();
            }else {
                reservationTime = 0;
                getMvpView().openReservationNotification();
                getMvpView().removeReservationInfo();
            }
        }else{
            getMvpView().removeReservationInfo();
            reservationTime = 0;
        }

        getMvpView().hideHCustomLoading();
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Car trip
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load from server car trip by plate.
     *
     * @param  plate  plate for car's trip.
     */
    public void loadCarsTrip(String plate) {

        hideLoading = true;

        mCarsReservationRequest = null;
        mCarsReservationRequest = buildCarsTripRequest(plate);
        addSubscription(mCarsReservationRequest.unsafeSubscribe(getCarsTripSubscriber()));
    }


    private Observable<ResponseCar> buildCarsTripRequest(String plate) {

        return mCarsReservationRequest = mCarRepository.getCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, plate, null, null, null,null)
                .first()
                .compose(this.<ResponseCar>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkCarTripResult();
                    }
                });
    }

    private Subscriber<ResponseCar> getCarsTripSubscriber(){
        return new Subscriber<ResponseCar>() {
            @Override
            public void onCompleted() {
                mCarsReservationRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCarsReservationRequest = null;
                //getMvpView().showError(e);

            }

            @Override
            public void onNext(ResponseCar responseList) {
                mResponseTripCar = responseList;
            }
        };
    }

    private void checkCarTripResult(){

        getMvpView().hideLoading();
        if((mResponseTripCar.reason.isEmpty() && mResponseTripCar.data != null) && (mResponseTrip.reason.isEmpty() && mResponseTrip.trips != null && mResponseTrip.trips.size() > 0)){

            //TODO Remove
            //mResponseReservationCar.data.parking = true;

            //Verifico se era in sosta e non è ancora passato un minuto
            if(isParked && !(seconds == 0 || ((System.currentTimeMillis() - seconds) / 1000) > 59)){
                mResponseReservationCar.data.parking = false;
            }

            isParked = false;
            getMvpView().showTripInfo(mResponseTripCar.data, mResponseTrip.trips.get(0).timestamp_start);
        }else{
            reservationTime = 0;
            getMvpView().removeReservationInfo();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              SET car selected (popover)
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set car to selected (popover).
     *
     * @param  cs  car to select.
     */
    public void setCarSelected(Car cs){
        mCarRepository.setCarSelected(cs);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              LOAD City
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Load city from server.
     *
     * @param  context  context of application.
     */
    public void loadCity(Context context) {

        hideLoading = true;

        if( mCityRequest == null) {
            mCityRequest = buildCitiesRequest(context);
            addSubscription(mCityRequest.unsafeSubscribe(getCitiesSubscriber(context)));
        }
    }

    private Observable<ResponseCity> buildCitiesRequest(final Context context) {
        return mCityRequest = mAppRepository.getCities(context)
                .first()
                .compose(this.<ResponseCity>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {


                        //Memorizzo le città sul dispositivo in caso di mancata connessione ad internet
                        SharedPreferences mPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPref.edit();
                        Type fooType = new TypeToken<List<City>>() {}.getType();
                        Gson gson = new Gson();
                        String json = gson.toJson(mCitiesList, fooType);
                        prefsEditor.putString(context.getString(R.string.preference_city), json);
                        prefsEditor.commit();

                        getMvpView().showCity(mCitiesList);
                    }
                });
    }

    private Subscriber<ResponseCity> getCitiesSubscriber(final Context context){
        return new Subscriber<ResponseCity>() {
            @Override
            public void onCompleted() {
                mCityRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCityRequest = null;

                //Provo a recuperare le città dal dispositivo
                SharedPreferences mPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
                Type fooType = new TypeToken<List<City>>() {}.getType();
                Gson gson = new Gson();
                String json = mPref.getString(context.getString(R.string.preference_city), "");
                List<City> obj = (ArrayList<City>) gson.fromJson(json, fooType);

                try {
                    if (obj != null) getMvpView().showCity(obj);
                }catch (NullPointerException er){}
            }

            @Override
            public void onNext(ResponseCity response) {
                mCitiesList = response.data;
            }
        };
    }



    /**
     * Retrieve from server user's trips list.
     */
    public void showNotificationOnLastTrip(){
        hideLoading = true;
        //getMvpView().showStandardLoading();

        if( mTripsRequestLast == null) {
            mTripsRequestLast = buildTripsRequestLast();
            addSubscription(mTripsRequestLast.unsafeSubscribe(getTripsSubscriberLast()));
        }
    }

    private Observable<ResponseTrip> buildTripsRequestLast() {

        return mTripsRequestLast = mUserRepository.getTripsLast(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, false, true,2)
                .first()
                .compose(this.<ResponseTrip>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkTripsResultLast();
                    }
                });
    }

    private Subscriber<ResponseTrip> getTripsSubscriberLast(){
        return new Subscriber<ResponseTrip>() {
            @Override
            public void onCompleted() {
                mTripsRequestLast = null;
            }

            @Override
            public void onError(Throwable e) {
                mTripsRequestLast = null;

                try {
                    if (isViewAttached())
                        getMvpView().openNotification(mResponseTripLast.trips.get(0).timestamp_start, mResponseTripLast.trips.get(0).timestamp_end);
                }catch (Exception ex){
                    Log.e(TAG, "onError: probailmente non ho i dati",ex );
                }
            }

            @Override
            public void onNext(ResponseTrip response) {
                mResponseTripLast = response;
            }
        };
    }

    private void checkTripsResultLast(){
        if(mResponseTripLast.reason.isEmpty() && mResponseTripLast.trips != null && mResponseTripLast.trips.size() > 0){

            if(!mResponseTripLast.trips.get(0).payable && mResponseTripLast.trips.get(0).getTimestampEndDiff() < 120) {
              if(isViewAttached())
                    getMvpView().openNotification(mResponseTripLast.trips.get(0).timestamp_start,mResponseTripLast.trips.get(0).timestamp_end,mResponseTripLast.trips.get(0).payable);
            }else{
                if(isViewAttached())
                     getMvpView().openNotification(mResponseTripLast.trips.get(0).timestamp_start,mResponseTripLast.trips.get(0).timestamp_end);
            }
        }else{
             if(isViewAttached())
                getMvpView().openNotification(mResponseTripLast.trips.get(0).timestamp_start,mResponseTripLast.trips.get(0).timestamp_end);
        }
    }

    public ResponseTrip getLastCurrentTrip(){
        return mUserRepository.getmCachedCurrentTrip();
    }



}



