package it.sharengo.development.ui.chronology;


import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class ChronologyPresenter extends BasePresenter<ChronologyMvpView> {

    private static final String TAG = ChronologyPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    private Observable<ResponseTrip> mTripsRequest;

    private ResponseTrip mResponseTrip;

    private boolean hideLoading;

    public ChronologyPresenter(SchedulerProvider schedulerProvider,
                               AppRepository appRepository,
                               UserRepository userRepository) {
        super(schedulerProvider);
        mAppRepository = appRepository;
        mUserRepository = userRepository;
        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {
        if(mResponseTrip.trips != null) {
            getMvpView().showList(mResponseTrip.trips);
        }
    }

    @Override
    protected boolean showCustomLoading() {
        /*if(hideLoading)
            return true;
        else
            return super.showCustomLoading();*/
        return true;
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
        return mTripsRequest = mUserRepository.getChronTrips(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password)
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
            //getMvpView().showStandardLoading();
            getMvpView().showList(mResponseTrip.trips);

        }else{

            getMvpView().showEmptyResult();
        }
    }

    public void hideLoading(){
        getMvpView().hideStandardLoading();
    }

}



