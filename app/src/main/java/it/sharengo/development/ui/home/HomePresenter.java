package it.sharengo.development.ui.home;

import android.util.Log;

import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.models.User;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
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

    public HomePresenter(SchedulerProvider schedulerProvider,
                         AppRepository appRepository,
                         UserRepository userRepository) {
        super(schedulerProvider);
        mUserRepository = userRepository;
        mAppRepository = appRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.HOME);
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

    public User getUserInfo(){
        return mUserRepository.getCachedUser();
    }

    public boolean animateHome(){
        return mAppRepository.animateHome();
    }

    public void setAnimateHome(boolean animate){
        mAppRepository.setAnimateHome(animate);
    }

}
