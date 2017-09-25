package it.sharengo.eteria.ui.login;


import android.content.SharedPreferences;
import android.util.Log;

import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.ResponseReservation;
import it.sharengo.eteria.data.models.ResponseTrip;
import it.sharengo.eteria.data.models.ResponseUser;
import it.sharengo.eteria.data.models.UserInfo;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.PreferencesRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;
    private final PreferencesRepository mPreferencesRepository;
    private Observable<ResponseReservation> mReservationsRequest;
    private Observable<ResponseTrip> mTripsRequest;

    /*
     *  REQUEST
     */
    private Observable<ResponseUser> mUserRequest;

    private UserInfo mCachedUser;


    public LoginPresenter(SchedulerProvider schedulerProvider,
                          AppRepository appRepository,
                          UserRepository userRepository,
                          PreferencesRepository preferencesRepository) {
        super(schedulerProvider, userRepository);

        mUserRepository = userRepository;
        mPreferencesRepository = preferencesRepository;
        mAppRepository = appRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.LOGIN);
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

    /**
     * Check with server if login and password it's correct.
     *
     * @param  username  username to check.
     * @param  password  password to check.
     */
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
                        if(mCachedUser.enabled) {
                            //Salvo le credenziali dell'utente
                            getMvpView().loginCompleted(username, password, mCachedUser);
                        }else{
                            getMvpView().showEnabledError();
                        }
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

                mCachedUser = response.user;
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              SAVE CREDENTIALS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Save credentials to preference and retrieve reservation of user from server.
     *
     * @param  username  username to save.
     * @param  password  password to save.
     * @param  mPref     preference of app.
     */
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
    private void getReservation(){

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

                getMvpView().navigateTo();
            }

            @Override
            public void onNext(ResponseReservation response) {

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
        return mTripsRequest = mUserRepository.getTrips(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, false, true) //TODO, deve essere true
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

                getMvpView().navigateTo();
            }

            @Override
            public void onNext(ResponseTrip response) {

            }
        };
    }

}



