package it.sharengo.development.ui.map;


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
import it.sharengo.development.data.models.Post;
import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.data.models.User;
import it.sharengo.development.data.repositories.AddressRepository;
import it.sharengo.development.data.repositories.AuthRepository;
import it.sharengo.development.data.repositories.CarRepository;
import it.sharengo.development.data.repositories.PostRepository;
import it.sharengo.development.data.repositories.PreferencesRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class MapPresenter extends BasePresenter<MapMvpView> {

    private static final String TAG = MapPresenter.class.getSimpleName();

    private final PostRepository mPostRepository;
    private final CarRepository mCarRepository;
    private final AddressRepository mAddressRepository;
    private final PreferencesRepository mPreferencesRepository;
    private final AuthRepository mAuthRepository;
    private final UserRepository mUserRepository;

    /*
     *  REQUEST
     */
    private Observable<List<Post>> mPostsRequest;
    private Observable<Response> mCarsRequest;
    private Observable<Response> mPlatesRequest;
    private Observable<List<Car>> mFindPlatesRequest;
    private Observable<List<Address>> mFindAddressRequest;
    private Observable<List<SearchItem>> mFindSearchRequest;
    private Observable<ResponseTrip> mTripsRequest;

    /*
     *  VAR
     */

    private Response mResponse;
    private ResponseTrip mResponseTrip;
    private List<Car> mPlates;
    private List<Address> mAddress;
    private List<SearchItem> mSearchItems;
    private List<SearchItem> historicItems;
    private List<Post> mPosts;
    private boolean hideLoading;

    /*
     *  Timer
     */
    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();


    public MapPresenter(SchedulerProvider schedulerProvider,
                        PostRepository postRepository,
                        CarRepository carRepository,
                        AddressRepository addressRepository,
                        PreferencesRepository preferencesRepository,
                        AuthRepository authRepository,
                        UserRepository userRepository) {
        super(schedulerProvider);
        mPostRepository = postRepository;
        mCarRepository = carRepository;
        mAddressRepository = addressRepository;
        mPreferencesRepository = preferencesRepository;
        mAuthRepository = authRepository;
        mUserRepository = userRepository;

        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);

    }


    @Override
    protected void restoreDataOnConfigurationChange() {
        startTimer();
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

    void viewCreated() {
        getTrips();
        loadPlates();
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
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
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

    public void refreshCars(float latitude, float longitude, int radius){
        hideLoading = true;
        loadCars(latitude, longitude, radius);
    }

    public void loadCars(float latitude, float longitude, int radius) {

        //if( mCarsRequest == null) {

            mCarsRequest = null;
            mCarsRequest = buildCarsRequest(latitude, longitude, radius);
            addSubscription(mCarsRequest.unsafeSubscribe(getCarsSubscriber()));
        //}
    }


    private Observable<Response> buildCarsRequest(float latitude, float longitude, int radius) {

        return mCarsRequest = mCarRepository.getCars(latitude, longitude, radius)
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
            if(!mResponse.reason.equals("No cars found"))
                getMvpView().showError(mResponse.reason);

            getMvpView().noCarsFound();
        }
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
        return mPlatesRequest = mCarRepository.getPlates()
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
        return mFindSearchRequest = mPreferencesRepository.getHistoricSearch(searchText, mPrefs)
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

                //TODO: preferiti
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
    //                                              Booking
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void bookingCar(Car car){
        getMvpView().showBookingCar();
    }

    public void openDoor(Car car){
        Trip trip = new Trip(car.id, car.latitude, car.longitude);
        getMvpView().setTripInfo(trip);
    }

    public void deleteBookingCar(){
        getMvpView().showConfirmDeletedCar();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Trips
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void getTrips(){
        if( mTripsRequest == null) {
            mTripsRequest = buildTripsRequest();
            addSubscription(mTripsRequest.unsafeSubscribe(getTripsSubscriber()));
        }
    }

    private Observable<ResponseTrip> buildTripsRequest() {
        return mTripsRequest = mUserRepository.getTrips(false)
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
            getMvpView().showTripInfo(mResponseTrip.trips.get(0));
        }else{

            getMvpView().removeTripInfo();
        }
    }
}



