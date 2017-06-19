package it.sharengo.development.ui.login;


import android.content.SharedPreferences;
import android.util.Log;

import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.PreferencesRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private final UserRepository mUserRepository;
    private final PreferencesRepository mPreferencesRepository;
    private Observable<ResponseReservation> mReservationsRequest;
    private Observable<ResponseTrip> mTripsRequest;

    /*
     *  REQUEST
     */
    private Observable<ResponseUser> mUserRequest;


    public LoginPresenter(SchedulerProvider schedulerProvider,
                          UserRepository userRepository,
                          PreferencesRepository preferencesRepository) {
        super(schedulerProvider);

        mUserRepository = userRepository;
        mPreferencesRepository = preferencesRepository;
        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    public void showError(Throwable throwable){
        getMvpView().showError(throwable);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Login
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void login(String username, String password){
        if( mUserRequest == null) {
            mUserRequest = buildUserRequest(username, password);
            addSubscription(mUserRequest.unsafeSubscribe(getUserSubscriber()));
        }
    }

    private Observable<ResponseUser> buildUserRequest(final String username, final String password) {
        return mUserRequest = mUserRepository.getUser(username, password)
                .first()
                .compose(this.<ResponseUser>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        //Salvo le credenziali dell'utente
                        getMvpView().loginCompleted(username, password);
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
                getMvpView().showLoginError(e);
            }

            @Override
            public void onNext(ResponseUser response) {

            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              SAVE CREDENTIALS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void saveCredentials(String username, String password, SharedPreferences mPref){
        mPreferencesRepository.saveCredentials(username, password, mPref);
        mUserRepository.saveUserCredentials(username, password);
        getReservation();
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
                getMvpView().navigateTo();
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
                        getMvpView().navigateTo();
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
                getMvpView().navigateTo();
            }

            @Override
            public void onNext(ResponseTrip response) {
                Log.w("Trip",": "+response.reason);
            }
        };
    }

}



