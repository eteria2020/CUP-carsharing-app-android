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

    }

    public boolean isAuth(){
        if(!mUserRepository.getCachedUser().username.isEmpty()) return true;
        return false;
    }

}
