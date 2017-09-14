package it.sharengo.eteria.ui.home;

import android.os.Handler;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import it.sharengo.eteria.data.models.City;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.ResponseReservation;
import it.sharengo.eteria.data.models.ResponseTrip;
import it.sharengo.eteria.data.models.ResponseUser;
import it.sharengo.eteria.data.models.User;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class HomePresenter extends BasePresenter<HomeMvpView> {

    private final UserRepository mUserRepository;
    private final AppRepository mAppRepository;

    /*
     *  REQUEST
     */
    private Observable<ResponseUser> mUserRequest;
    private Observable<ResponseReservation> mReservationsRequest;
    private Observable<ResponseTrip> mTripsRequest;

    private ResponseReservation mResponseReservation;
    private ResponseTrip mResponseTrip;

    private Timer timer1min;
    private TimerTask timerTask1min;
    private final Handler handler1min = new Handler();
    private boolean isTripExists;
    private boolean isBookingExists;
    private int timestamp_start;
    private boolean hideLoading;

    public HomePresenter(SchedulerProvider schedulerProvider,
                         AppRepository appRepository,
                         UserRepository userRepository) {
        super(schedulerProvider, userRepository);
        mUserRepository = userRepository;
        mAppRepository = appRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }

    @Override
    protected void restoreDataOnConfigurationChange() {

    }

    @Override
    protected void subscribeRequestsOnResume() {
        startTimer();
    }

    @Override
    protected boolean showCustomLoading() {
        if(hideLoading)
            return true;
        else
            return super.showCustomLoading();
    }

    void viewCreated() {
        hideLoading = true;
    }

    void viewDestroy() {
        stoptimertask();
    }

    /**
     * Start timer for check reservation or course available.
     */
    public void startTimer() {

        //1 minuto
        timer1min = new Timer();

        timerTask1min = new TimerTask() {
            public void run() {

                handler1min.post(new Runnable() {
                    public void run() {


                        if(mUserRepository.getCachedUser() != null && !mUserRepository.getCachedUser().username.isEmpty())
                            getReservations(true);

                    }
                });
            }
        };

        timer1min.schedule(timerTask1min, 3000, 20000); //60000 TODO

    }

    /**
     * Stop timer
     */
    public void stoptimertask() {

        if (timer1min != null) {
            timer1min.cancel();
            timer1min = null;
        }
    }

    /**
     * Check if user is authenticated.
     *
     * @return      result of check user authentication.
     * @see         boolean
     */
    public boolean isAuth(){
        if(mUserRepository.getCachedUser() != null && !mUserRepository.getCachedUser().username.isEmpty()) return true;
        return false;
    }

    /**
     * Retrieve from cache information of user authenticated.
     *
     * @return      user object.
     * @see         User
     */
    public User getUserInfo(){
        return mUserRepository.getCachedUser();
    }

    /**
     * Check status home's animation.
     *
     * @return      status of animation of home.
     * @see         boolean
     */
    public boolean animateHome(){
        return mAppRepository.animateHome();
    }

    /**
     * Save status home's animation (if need execute or not execute).
     *
     * @param  animate  boolean of status animation.
     */
    public void setAnimateHome(boolean animate){
        mAppRepository.setAnimateHome(animate);
    }

    /**
     * Retrieve preference of city.
     *
     * @return      object with city's preference.
     * @see         City
     */
    public City getCityPreference(){
        return mAppRepository.getCityPreference();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Reservations
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Retrieve from server auto's reservation.
     *
     * @param  refreshInfo  boolean if refresh information.
     */
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
    //                                              GET Trips
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve from server trips available.
     *
     * @param  refreshInfo  boolean if refresh information.
     */
    void getTrips(boolean refreshInfo){

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

            timestamp_start = mResponseTrip.trips.get(0).timestamp_start;
            isTripExists = true;
        }else{
            if(isTripExists){
                isTripExists = false;

                getMvpView().openNotification(timestamp_start, (int) (System.currentTimeMillis() / 1000L));
            }
        }
    }

}
