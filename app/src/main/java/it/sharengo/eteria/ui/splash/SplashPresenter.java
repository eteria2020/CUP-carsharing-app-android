package it.sharengo.eteria.ui.splash;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.ResponseCity;
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

import static android.content.Context.MODE_PRIVATE;

public class SplashPresenter extends BasePresenter<SplashMvpView> {

    private final PreferencesRepository mPreferencesRepository;
    private final UserRepository mUserRepository;
    private final AppRepository mAppRepository;

    private Observable<Object> mSplashRequest;
    private Observable<ResponseUser> mUserRequest;
    private Observable<ResponseReservation> mReservationsRequest;
    private Observable<ResponseTrip> mTripsRequest;
    private Observable<ResponseCity> mCityRequest;

    private Context mContext;

    public SplashPresenter(SchedulerProvider schedulerProvider, PreferencesRepository preferencesRepository, UserRepository userRepository, AppRepository appRepository) {
        super(schedulerProvider, userRepository);
        mPreferencesRepository = preferencesRepository;
        mUserRepository = userRepository;
        mAppRepository = appRepository;
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

    /**
     * Load data from preference.
     *
     * @param  mPref   shared preference of app
     * @param  context context of application
     */
    public void loadData(SharedPreferences mPref, Context context) {

        mContext = context;


        //Recupero le credenziali dell'utente (se salvate)
        mUserRepository.saveUserCredentials(mPreferencesRepository.getUsername(mPref), mPreferencesRepository.getPassword(mPref));


        //Recupero la lingua impostata
        if(mUserRepository.getCachedUser() != null && !mUserRepository.getCachedUser().username.isEmpty()) //Utente loggato
            mAppRepository.putLang(mPref.getString(context.getString(R.string.preference_lang), Locale.getDefault().getLanguage()));
        else
            mAppRepository.putLang(Locale.getDefault().getLanguage());


        if(mSplashRequest == null) {

            if(mUserRepository.getCachedUser() != null && !mUserRepository.getCachedUser().username.isEmpty()) { //Utente loggato
                //Recupero le informazioni dell'utente
                getUser(context);
                getTrips();
                getCities();

                //Provo a prelevare i dati dell'utente dalle preferenze
                mPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
                Type fooType = new TypeToken<UserInfo>() {}.getType();
                Gson gson = new Gson();
                String json = mPref.getString(context.getString(R.string.preference_userinfo), "");
                UserInfo obj = (UserInfo) gson.fromJson(json, fooType);
                if(obj != null && mUserRepository.getCachedUser() != null) mUserRepository.getCachedUser().userInfo = obj;

                askPermission();

            }else{ //Utente non loggato

                mSplashRequest = Observable
                        .just(new Object())
                        .delay(2, TimeUnit.SECONDS)
                        .compose(this.handleDataRequest())
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                askPermission();
                            }
                        });

                addSubscription(mSplashRequest.subscribe());
            }
        }
    }

    /**
     * Check if first access to app.
     *
     * @param  mPrefs  shared preference of app
     * @return         true if first access to app
     * @see            boolean
     */
    public boolean isFirstAccess(SharedPreferences mPrefs){
        return mPreferencesRepository.getFirstAccess(mPrefs);
    }

    /**
     * Set boolean of first access to app at false.
     *
     * @param  mPrefs  shared preference of app
     */
    public void setFirstAccess(SharedPreferences mPrefs){
        mPreferencesRepository.saveFirstAccess(false, mPrefs);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              User info
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void getUser(Context context){
        if( mUserRequest == null) {
            mUserRequest = buildUserRequest();
            addSubscription(mUserRequest.unsafeSubscribe(getUserSubscriber(context)));
        }
    }

    private Observable<ResponseUser> buildUserRequest() {


        return mUserRequest = mUserRepository.getUser(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password)
                .first()
                .compose(this.<ResponseUser>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        //getReservation();
                        //getMvpView().navigateToHome(mAppRepository.getLang());
                    }
                });
    }

    private Subscriber<ResponseUser> getUserSubscriber(final Context context){
        return new Subscriber<ResponseUser>() {
            @Override
            public void onCompleted() {
                mUserRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mUserRequest = null;

            }

            @Override
            public void onNext(ResponseUser response) {

            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Reservation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve from server all reservation of user.
     */
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
                        //getCities();
                        //getTrips();
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

                getMvpView().checkMapPermission();
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

    /**
     * Retrieve from server all trips of user.
     */
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

                //getMvpView().navigateToHome(mAppRepository.getLang());
            }

            @Override
            public void onNext(ResponseTrip response) {

            }
        };
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Cities
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve from server all cities.
     */
    void getCities(){
        if( mCityRequest == null) {
            mCityRequest = buildCitiesRequest();
            addSubscription(mCityRequest.unsafeSubscribe(getCitiesSubscriber()));
        }
    }

    private Observable<ResponseCity> buildCitiesRequest() {
        return mCityRequest = mAppRepository.getCities(mContext)
                .first()
                .compose(this.<ResponseCity>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        //getMvpView().navigateToHome(mAppRepository.getLang());
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

                //getMvpView().navigateToHome(mAppRepository.getLang());
            }

            @Override
            public void onNext(ResponseCity response) {

            }
        };
    }

    public void permissionChecked(){
        getMvpView().navigateToHome(mAppRepository.getLang());
    }

    public void askPermission(){
        try{
            getMvpView().checkMapPermission();
        }catch(Exception e){

        }
    }
}
