package it.sharengo.eteria.ui.chronology;


import android.util.Log;

import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.ResponseTrip;
import it.sharengo.eteria.data.models.ResponseUser;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class ChronologyPresenter extends BasePresenter<ChronologyMvpView> {

    private static final String TAG = ChronologyPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    private Observable<ResponseTrip> mTripsRequest;
    private Observable<ResponseUser> mUserRequest;

    private ResponseTrip mResponseTrip;
    private ResponseUser responseUser;

    private float discount_rate = 0.28f;

    private boolean hideLoading;

    public ChronologyPresenter(SchedulerProvider schedulerProvider,
                               AppRepository appRepository,
                               UserRepository userRepository) {
        super(schedulerProvider,userRepository);
        mAppRepository = appRepository;
        mUserRepository = userRepository;
        mAppRepository.selectMenuItem(MenuItem.Section.HISTORIC);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {
        if(mResponseTrip != null && mResponseTrip.trips != null) {
            getMvpView().showList(mResponseTrip.trips, discount_rate);
        }else{
            getTrips();
        }
    }

    @Override
    protected boolean showCustomLoading() {
        /*if(hideLoading)
            return true;
        else
            return super.showCustomLoading();*/
        return false;
    }


    @Override
    protected void subscribeRequestsOnResume() {

        getTrips();
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Trips
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve from server user's trips list.
     */
    public void getTrips(){

        hideLoading = false;
        //getMvpView().showStandardLoading();

        if( mTripsRequest == null) {
            mTripsRequest = buildTripsRequest();
            addSubscription(mTripsRequest.unsafeSubscribe(getTripsSubscriber()));
        }
    }

    private Observable<ResponseTrip> buildTripsRequest() {
        return mTripsRequest = mUserRepository.getTrips(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, false, false)
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
                getMvpView().showChronError(e);
            }

            @Override
            public void onNext(ResponseTrip response) {
                mResponseTrip = response;
            }
        };
    }

    private void checkTripsResult(){
        if(mResponseTrip.reason.isEmpty() && mResponseTrip.trips != null && mResponseTrip.trips.size() > 0){

            getRatesInfo();
        }else{

            getMvpView().showEmptyResult();
        }
    }

    public void hideLoading(){
        getMvpView().hideStandardLoading();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET User info
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void getRatesInfo(){
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

                        if(responseUser.reason != null && responseUser.reason.isEmpty() && responseUser.user != null){
                            discount_rate = responseUser.user.discount_rate;
                        }

                        getMvpView().showList(mResponseTrip.trips, discount_rate);
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
                getMvpView().showList(mResponseTrip.trips, 0.28f);
            }

            @Override
            public void onNext(ResponseUser response) {

                responseUser = response;
            }
        };
    }

}



