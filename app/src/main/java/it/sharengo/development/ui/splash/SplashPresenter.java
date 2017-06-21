package it.sharengo.development.ui.splash;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.repositories.PreferencesRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.StringsUtils;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

import static android.content.Context.MODE_PRIVATE;

public class SplashPresenter extends BasePresenter<SplashMvpView> {

    private final PreferencesRepository mPreferencesRepository;
    private final UserRepository mUserRepository;

    private Observable<Object> mSplashRequest;
    private Observable<ResponseUser> mUserRequest;
    private Observable<ResponseReservation> mReservationsRequest;
    private Observable<ResponseTrip> mTripsRequest;

    public SplashPresenter(SchedulerProvider schedulerProvider, PreferencesRepository preferencesRepository, UserRepository userRepository) {
        super(schedulerProvider);
        mPreferencesRepository = preferencesRepository;
        mUserRepository = userRepository;
    }

    @Override
    protected void restoreDataOnConfigurationChange() {

    }

    @Override
    protected void subscribeRequestsOnResume() {
        if (mSplashRequest != null) {
            addSubscription(mSplashRequest.subscribe());
        }
    }

    @Override
    protected boolean showCustomLoading() {
        return true;
    }

    public void loadData(SharedPreferences mPref) {

        Log.w("LOAD","DATA");

        //Recupero le credenziali dell'utente (se salvate)

        Log.w("user1",": "+mPreferencesRepository.getFirstAccess(mPref));

        mUserRepository.saveUserCredentials(mPreferencesRepository.getUsername(mPref), mPreferencesRepository.getPassword(mPref));

        if(mSplashRequest == null) {

            Log.w("user2",": "+mUserRepository.getCachedUser().username);
            if(!mUserRepository.getCachedUser().username.isEmpty()) { //Utente loggato
                //Recupero le informazioni dell'utente
                getUser();

            }else{ //Utente non loggato

                mSplashRequest = Observable
                        .just(new Object())
                        .delay(2, TimeUnit.SECONDS)
                        .compose(this.handleDataRequest())
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                getMvpView().navigateToHome();
                            }
                        });

                addSubscription(mSplashRequest.subscribe());
            }
        }
    }

    public boolean isFirstAccess(SharedPreferences mPrefs){
        return mPreferencesRepository.getFirstAccess(mPrefs);
    }

    public void setFirstAccess(SharedPreferences mPrefs){
        mPreferencesRepository.saveFirstAccess(false, mPrefs);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              User info
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUser(){
        Log.w("getUser","GET");
        if( mUserRequest == null) {
            mUserRequest = buildUserRequest();
            addSubscription(mUserRequest.unsafeSubscribe(getUserSubscriber()));
        }
    }

    private Observable<ResponseUser> buildUserRequest() {


        return mUserRequest = mUserRepository.getUser(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password)
                .first()
                .compose(this.<ResponseUser>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getReservation();
                    }
                });
    }

    private Subscriber<ResponseUser> getUserSubscriber(){
        return new Subscriber<ResponseUser>() {
            @Override
            public void onCompleted() {
                mUserRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mUserRequest = null;
                Log.w("User",": error");
                getMvpView().navigateToHome();
            }

            @Override
            public void onNext(ResponseUser response) {
                Log.w("User",": "+response.reason);
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Reservation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void getReservation(){

        if( mReservationsRequest == null) {
            mReservationsRequest = buildReservationsRequest();
            addSubscription(mReservationsRequest.unsafeSubscribe(getReservationsSubscriber()));
        }
    }

    private Observable<ResponseReservation> buildReservationsRequest() {
        return mReservationsRequest = mUserRepository.getReservations(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, false)
                .first()
                .compose(this.<ResponseReservation>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getTrips();
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
                Log.w("Reservation",": error");
                getMvpView().navigateToHome();
            }

            @Override
            public void onNext(ResponseReservation response) {
                Log.w("Reservation",": "+response.reason);
            }
        };
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
        return mTripsRequest = mUserRepository.getTrips(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, true, true) //TODO, deve essere true
                .first()
                .compose(this.<ResponseTrip>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getMvpView().navigateToHome();
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
                Log.w("Trip",": error");
                getMvpView().navigateToHome();
            }

            @Override
            public void onNext(ResponseTrip response) {
                Log.w("Trip",": "+response.reason);
            }
        };
    }
}
