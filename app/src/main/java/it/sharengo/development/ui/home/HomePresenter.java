package it.sharengo.development.ui.home;

import android.util.Log;

import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class HomePresenter extends BasePresenter<HomeMvpView> {

    private final UserRepository mUserRepository;

    /*
     *  REQUEST
     */
    private Observable<ResponseUser> mUserRequest;
    private Observable<ResponseReservation> mReservationsRequest;
    private Observable<ResponseTrip> mTripsRequest;

    public HomePresenter(SchedulerProvider schedulerProvider,
                         UserRepository userRepository) {
        super(schedulerProvider);
        mUserRepository = userRepository;
    }

    @Override
    protected void restoreDataOnConfigurationChange() {

    }

    @Override
    protected void subscribeRequestsOnResume() {

    }

    void viewCreated() {

        getUser();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              User info
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void getUser(){
        if( mUserRequest == null) {
            mUserRequest = buildUserRequest();
            addSubscription(mUserRequest.unsafeSubscribe(getUserSubscriber()));
        }
    }

    private Observable<ResponseUser> buildUserRequest() {
        return mUserRequest = mUserRepository.getUser()
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
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(ResponseUser response) {
                Log.w("pin",": "+response.user);
                Log.w("status",": "+response.status);
                Log.w("reason",": "+response.reason);
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
        return mReservationsRequest = mUserRepository.getReservations(false)
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
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(ResponseReservation response) {
                Log.w("reservation",": "+response.reservations);
                Log.w("status",": "+response.status);
                Log.w("reason",": "+response.reason);

                //Log.w("id",": "+response.reservations.get(0).id);
                //Log.w("length",": "+response.reservations.get(0).length);
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
        return mTripsRequest = mUserRepository.getTrips(true, true) //TODO, deve essere true
                .first()
                .compose(this.<ResponseTrip>handleDataRequest());
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
                Log.w("trips",": "+response.trips);
                Log.w("status",": "+response.status);
                Log.w("reason",": "+response.reason);
            }
        };
    }
}
