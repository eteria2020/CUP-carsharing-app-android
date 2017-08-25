package it.sharengo.development.ui.splash;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.sharengo.development.R;
import it.sharengo.development.data.models.ResponseCity;
import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.models.UserInfo;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.PreferencesRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
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

            }else{ //Utente non loggato

                mSplashRequest = Observable
                        .just(new Object())
                        .delay(2, TimeUnit.SECONDS)
                        .compose(this.handleDataRequest())
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                getMvpView().navigateToHome(mAppRepository.getLang());
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
                        getReservation();
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
                Log.w("User",": error");

                //Provo a prelevare i dati dell'utente dalle preferenze
                SharedPreferences mPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
                Type fooType = new TypeToken<UserInfo>() {}.getType();
                Gson gson = new Gson();
                String json = mPref.getString(context.getString(R.string.preference_userinfo), "");
                UserInfo obj = (UserInfo) gson.fromJson(json, fooType);
                if(obj != null && mUserRepository.getCachedUser() != null) mUserRepository.getCachedUser().userInfo = obj;

                getMvpView().navigateToHome(mAppRepository.getLang());
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
                getMvpView().navigateToHome(mAppRepository.getLang());
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
        return mTripsRequest = mUserRepository.getTrips(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, true, true) //TODO, deve essere true
                .first()
                .compose(this.<ResponseTrip>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getCities();
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
                getMvpView().navigateToHome(mAppRepository.getLang());
            }

            @Override
            public void onNext(ResponseTrip response) {
                Log.w("Trip",": "+response.reason);
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
                        getMvpView().navigateToHome(mAppRepository.getLang());
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
                Log.w("Cities",": error");
                getMvpView().navigateToHome(mAppRepository.getLang());
            }

            @Override
            public void onNext(ResponseCity response) {
                Log.w("Cities",": "+response.status);
            }
        };
    }
}
