package it.sharengo.development.ui.mapgoogle;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.sharengo.development.R;
import it.sharengo.development.data.models.Address;
import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.Feed;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.models.Post;
import it.sharengo.development.data.models.Reservation;
import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseCar;
import it.sharengo.development.data.models.ResponseCity;
import it.sharengo.development.data.models.ResponseFeed;
import it.sharengo.development.data.models.ResponsePutReservation;
import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.data.models.User;
import it.sharengo.development.data.repositories.AddressRepository;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.CarRepository;
import it.sharengo.development.data.repositories.CityRepository;
import it.sharengo.development.data.repositories.PostRepository;
import it.sharengo.development.data.repositories.PreferencesRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.map.BaseMapPresenter;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class MapGooglePresenter extends BaseMapPresenter<MapGoogleMvpView> {

    private static final String TAG = MapGooglePresenter.class.getSimpleName();

    private Boolean mMapIsReady = false;
    private Boolean mLocationIsReady = false;
    private Double mLatitude;
    private Double mLongitude;

    private final AppRepository mAppRepository;
    private final PostRepository mPostRepository;
    private final CarRepository mCarRepository;
    private final AddressRepository mAddressRepository;
    private final PreferencesRepository mPreferencesRepository;
    private final UserRepository mUserRepository;
    private final CityRepository mCityRepository;

    /*
     *  REQUEST
     */
    private Observable<List<Post>> mPostsRequest;
    private Observable<Response> mCarsRequest;
    private Observable<ResponseCar> mCarsReservationRequest;
    private Observable<ResponseCar> mCarsTripRequest;
    private Observable<Response> mPlatesRequest;
    private Observable<List<Car>> mFindPlatesRequest;
    private Observable<List<Address>> mFindAddressRequest;
    private Observable<List<SearchItem>> mFindSearchRequest;
    private Observable<ResponseTrip> mTripsRequest;
    private Observable<ResponseReservation> mReservationsRequest;
    private Observable<ResponsePutReservation> mReservationRequest;
    private Observable<ResponseCity> mCityRequest;
    private Observable<ResponseFeed> mFeedRequest;

    /*
     *  VAR
     */

    private Response mResponse;
    private ResponseCar mResponseReservationCar;
    private ResponseTrip mResponseTrip;
    private ResponseReservation mResponseReservation;
    private Reservation mReservation;
    private List<Car> mPlates;
    private List<Address> mAddress;
    private List<SearchItem> mSearchItems;
    private List<SearchItem> historicItems;
    private List<Post> mPosts;
    private boolean hideLoading;
    private boolean isTripExists;
    private boolean isBookingExists;
    private int timestamp_start;
    private List<City> mCitiesList;
    public boolean isFeeds;
    private List<Feed> mOffersList;
    private List<Feed> mEventsList;

    /*
     *  Timer
     */
    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();

    private Timer timer1min;
    private TimerTask timerTask1min;
    private final Handler handler1min = new Handler();


    public MapGooglePresenter(SchedulerProvider schedulerProvider,
                              AppRepository appRepository,
                              PostRepository postRepository,
                              CarRepository carRepository,
                              AddressRepository addressRepository,
                              PreferencesRepository preferencesRepository,
                              UserRepository userRepository,
                              CityRepository cityRepository) {
        super(schedulerProvider);
        mPostRepository = postRepository;
        mCarRepository = carRepository;
        mAddressRepository = addressRepository;
        mPreferencesRepository = preferencesRepository;
        mUserRepository = userRepository;
        mAppRepository = appRepository;
        mCityRepository = cityRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.BOOKING);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    @Override
    protected boolean showCustomLoading() {
        if(hideLoading)
            return true;
        else
            return super.showCustomLoading();
    }

    public void onMapIsReady() {
        mMapIsReady = true;
        Log.w("onMapIsReady","YES");
    }

    public void onLocationIsReady(Double lat, Double lng) {
        mLocationIsReady = true;
        mLatitude = lat;
        mLongitude = lng;
        Log.w("onLocationIsReady","YES");
    }




    void viewCreated() {

        isTripExists = false;
        isBookingExists = false;

        loadPlates();

        loadCarpopup();

        if(!mUserRepository.getCachedUser().username.isEmpty())
            getReservations(false);

        startTimer();
    }

    void viewDestroy() {
        stoptimertask();
    }


    public void startTimer() {

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

        timer.schedule(timerTask, 300000, 300000);


        //1 minuto
        timer1min = new Timer();

        timerTask1min = new TimerTask() {
            public void run() {

                handler1min.post(new Runnable() {
                    public void run() {

                        Log.w("PASSATO","1 MINUTO");
                        if(!mUserRepository.getCachedUser().username.isEmpty())
                            getReservations(true);

                        //getTrips(true);

                        //getMvpView().openTripEnd(1497253171);
                        /*getMvpView().openNotification(1497257941, (int) (System.currentTimeMillis() / 1000L));
                        timerTask1min.cancel();
                        timer.cancel();*/
                    }
                });
            }
        };

        timer1min.schedule(timerTask1min, 60000, 60000); //60000 TODO

    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (timer1min != null) {
            timer1min.cancel();
            timer1min = null;
        }
    }

    public boolean isAuth(){
        if(!mUserRepository.getCachedUser().username.isEmpty()) return true;
        return false;
    }

    private void loadCarpopup(){
        if(mCarRepository.getCarSelected() != null)
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

    public void refreshCars(Context context, float latitude, float longitude, int radius){
        hideLoading = true;
        loadCars(latitude, longitude, radius);
        if(isFeeds)
            loadFeeds(context, latitude, longitude, radius);
    }

    public void loadCars(float latitude, float longitude, int radius) {

        if( mCarsRequest == null) {

            mCarsRequest = null;
            mCarsRequest = buildCarsRequest(latitude, longitude, radius);
            addSubscription(mCarsRequest.unsafeSubscribe(getCarsSubscriber()));
        }
    }


    private Observable<Response> buildCarsRequest(float latitude, float longitude, int radius) {
        return mCarsRequest = mCarRepository.getCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, latitude, longitude, radius)
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
                getMvpView().showError(e);
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
    //                                              Load Offers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

        Log.w("EVENT","EV");
        for(Feed ff : mEventsList){
            Log.w("E",": "+ff.title);
        }

        if(!mOffersList.isEmpty())
            feeds.addAll(mOffersList);

        Log.w("OFFER","OF");
        for(Feed ff : mOffersList){
            Log.w("O",": "+ff.title);
        }

        getMvpView().showFeeds(feeds);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Load Plates
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadPlates() {
        hideLoading = true;

        if( mPlatesRequest == null) {
            mPlatesRequest = buildPlatesRequest();
            addSubscription(mPlatesRequest.unsafeSubscribe(getPlatesSubscriber()));
        }
    }


    private Observable<Response> buildPlatesRequest() {
        return mPlatesRequest = mCarRepository.getPlates(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password)
                .first()
                .compose(this.<Response>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkResult();
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
            public void onNext(Response responseList) {}
        };
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Plates
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    //                                              Find Address
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void findAddress(String searchText) {
        hideLoading = true;

        if( mFindAddressRequest == null) {
            mFindAddressRequest = buildFindAddressRequest(searchText);
            addSubscription(mFindAddressRequest.unsafeSubscribe(getFindAddressSubscriber()));
        }
    }


    private Observable<List<Address>> buildFindAddressRequest(String searchText) {
        return mFindAddressRequest = mAddressRepository.searchAddress(searchText,"json")
                .first()
                .compose(this.<List<Address>>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkAddressResult();
                    }
                });
    }

    private Subscriber<List<Address>> getFindAddressSubscriber(){
        return new Subscriber<List<Address>>() {
            @Override
            public void onCompleted() {
                mFindAddressRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFindAddressRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(List<Address> addressList) {
                mAddress = addressList;
            }
        };
    }

    private void checkAddressResult(){

        mSearchItems = new ArrayList<SearchItem>();

        for (Address address : mAddress){

            String type = "address";

            if(historicItems != null)
                for (SearchItem sI : historicItems) {
                    if (address.display_name.equals(sI.display_name)) type = "historic";
                }

            mSearchItems.add(new SearchItem(address.display_name, type, address.longitude, address.latitude));
        }

        getMvpView().showSearchResult(mSearchItems);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Search Item
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    public void saveSearchResultOnHistoric(SharedPreferences mPref, SearchItem searchItem){
        mPreferencesRepository.saveSearchResultOnHistoric(mPref, searchItem);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              User
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public User getUser(){
        return mUserRepository.getCachedUser();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Booking car
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void bookingCar(Car car, Context context){

        hideLoading = false;

        if( mReservationRequest == null) {
            mReservationRequest = buildReservationRequest(car);
            addSubscription(mReservationRequest.unsafeSubscribe(getReservationSubscriber(context)));
        }
    }

    private Observable<ResponsePutReservation> buildReservationRequest(Car car) {
        return mReservationRequest = mUserRepository.postReservations(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, car.id)
                .first()
                .compose(this.<ResponsePutReservation>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if(mReservation != null)

                            getReservations(true);
                    }
                });
    }

    private Subscriber<ResponsePutReservation> getReservationSubscriber(final Context context){
        return new Subscriber<ResponsePutReservation>() {
            @Override
            public void onCompleted() {
                mReservationRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mReservationRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(ResponsePutReservation response) {

                if(!response.reason.isEmpty() && response.reason.equals("Reservation created successfully"))
                    mReservation = response.reservation;
                else {
                    mReservation = null;
                    getMvpView().showError(context.getString(R.string.booking_alreadybookedcar_alert)); // Error: reservation:false - status:false - trip:false - limit:false - limit_archive:true
                }
            }
        };
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Delete booking
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void deleteBookingCar(int id){

        hideLoading = false;

        isBookingExists = false;

        if( mReservationRequest == null) {
            mReservationRequest = buildDeleteReservationRequest(id);
            addSubscription(mReservationRequest.unsafeSubscribe(getDeleteReservationSubscriber()));
        }
    }

    private Observable<ResponsePutReservation> buildDeleteReservationRequest(int id) {
        return mReservationRequest = mUserRepository.deleteReservations(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, id)
                .first()
                .compose(this.<ResponsePutReservation>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getMvpView().showConfirmDeletedCar();
                        getReservations(true);
                    }
                });
    }

    private Subscriber<ResponsePutReservation> getDeleteReservationSubscriber(){
        return new Subscriber<ResponsePutReservation>() {
            @Override
            public void onCompleted() {
                mReservationRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mReservationRequest = null;
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

    public void openDoor(Car car, String action) {

        if( mCarsTripRequest == null) {

            mCarsTripRequest = null;
            mCarsTripRequest = buildCarsOpenRequest(car, action);
            addSubscription(mCarsTripRequest.unsafeSubscribe(getCarsOpenSubscriber()));
        }
    }


    private Observable<ResponseCar> buildCarsOpenRequest(final Car car, final String action) {

        return mCarsTripRequest = mCarRepository.openCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, car.id, action)
                .first()
                .compose(this.<ResponseCar>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {

                        loadCarsTrip(car.id);
                    }
                });
    }

    private Subscriber<ResponseCar> getCarsOpenSubscriber(){
        return new Subscriber<ResponseCar>() {
            @Override
            public void onCompleted() {
                mCarsTripRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCarsTripRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(ResponseCar responseList) {
                //mResponseReservationCar = responseList;
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Trips
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void getTrips(boolean refreshInfo){

        //hideLoading = false;

        if( mTripsRequest == null) {
            mTripsRequest = buildTripsRequest(refreshInfo);
            addSubscription(mTripsRequest.unsafeSubscribe(getTripsSubscriber()));
        }
    }

    private Observable<ResponseTrip> buildTripsRequest(boolean refreshInfo) {
        return mTripsRequest = mUserRepository.getTrips(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, true, refreshInfo) //TODO, il valore deve essere true
                .first()
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
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(ResponseTrip response) {
                mResponseTrip = response;
            }
        };
    }

    private void checkTripsResult(){

        if(mResponseTrip.reason.isEmpty() && mResponseTrip.trips != null && mResponseTrip.trips.size() > 0){
            loadCarsTrip(mResponseTrip.trips.get(0).plate);

            timestamp_start = mResponseTrip.trips.get(0).timestamp_start;
            isTripExists = true;
        }else{

            if(isTripExists){
                isTripExists = false;

                getMvpView().openNotification(timestamp_start, (int) (System.currentTimeMillis() / 1000L));
                //timerTask1min.cancel();
                //timer.cancel();
            }
            getMvpView().removeTripInfo();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Reservations
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void getReservations(boolean refreshInfo){

        if( mReservationsRequest == null) {
            mReservationsRequest = buildReservationsRequest(refreshInfo);
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

    private Subscriber<ResponseReservation> getReservationsSubscriber(){
        return new Subscriber<ResponseReservation>() {
            @Override
            public void onCompleted() {
                mReservationsRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mReservationsRequest = null;
            }

            @Override
            public void onNext(ResponseReservation response) {
                mResponseReservation = response;
            }
        };
    }

    private void checkReservationsResult(){

        if(mResponseReservation.reason.isEmpty() && mResponseReservation.reservations != null && mResponseReservation.reservations.size() > 0){
            loadCarsReservation(mResponseReservation.reservations.get(0).car_plate);

            isBookingExists = true;
        }else{
            //getMvpView().removeReservationInfo();
            if(isBookingExists){
                isBookingExists = false;
                getMvpView().openReservationNotification();
            }
            getTrips(true);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Car reservation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadCarsReservation(String plate) {

        if( mCarsReservationRequest == null) {

            mCarsReservationRequest = null;
            mCarsReservationRequest = buildCarsReservationRequest(plate);
            addSubscription(mCarsReservationRequest.unsafeSubscribe(getCarsReservationSubscriber()));
        }
    }


    private Observable<ResponseCar> buildCarsReservationRequest(String plate) {

        return mCarsReservationRequest = mCarRepository.getCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, plate)
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


            if((mResponseReservation.reservations.get(0).length - diffTime) * 1000 > 0)
                getMvpView().showReservationInfo(mResponseReservationCar.data, mResponseReservation.reservations.get(0));
            else {
                getMvpView().openReservationNotification();
                getMvpView().removeReservationInfo();
            }
        }else{
            getMvpView().removeReservationInfo();
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Car trip
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadCarsTrip(String plate) {

        hideLoading = false;

        mCarsReservationRequest = null;
        mCarsReservationRequest = buildCarsTripRequest(plate);
        addSubscription(mCarsReservationRequest.unsafeSubscribe(getCarsTripSubscriber()));
    }


    private Observable<ResponseCar> buildCarsTripRequest(String plate) {

        return mCarsReservationRequest = mCarRepository.getCars(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, plate)
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
                mResponseReservationCar = responseList;
            }
        };
    }

    private void checkCarTripResult(){

        if(mResponseReservationCar.reason.isEmpty() && mResponseReservationCar.data != null){
            getMvpView().showTripInfo(mResponseReservationCar.data, timestamp_start);
        }else{
            getMvpView().removeReservationInfo();
        }

        //getTrips(true);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              SET car selected (popover)
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setCarSelected(Car cs){
        mCarRepository.setCarSelected(cs);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              LOAD City
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void loadCity(Context context) {

        hideLoading = true;

        if( mCityRequest == null) {
            mCityRequest = buildCitiesRequest(context);
            addSubscription(mCityRequest.unsafeSubscribe(getCitiesSubscriber()));
        }
    }

    private Observable<ResponseCity> buildCitiesRequest(Context context) {
        return mCityRequest = mAppRepository.getCities(context)
                .first()
                .compose(this.<ResponseCity>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getMvpView().showCity(mCitiesList);
                    }
                });
    }

    private Subscriber<ResponseCity> getCitiesSubscriber(){
        return new Subscriber<ResponseCity>() {
            @Override
            public void onCompleted() {
                mCityRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCityRequest = null;
            }

            @Override
            public void onNext(ResponseCity response) {
                mCitiesList = response.data;
            }
        };
    }



}



