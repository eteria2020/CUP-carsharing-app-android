package it.sharengo.eteria.ui.pin;


import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.ResponseUser;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class PinPresenter extends BasePresenter<PinMvpView> {

    private static final String TAG = PinPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    private Observable<ResponseUser> mUserRequest;

    private ResponseUser responseUser;

    public PinPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);
        mAppRepository = appRepository;
        mUserRepository = userRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.PIN);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    public int getPinInfo(){

     return  mUserRepository.getCachedUser().userInfo.pin;

    }

    private Observable<ResponseUser> buildUserRequest() {
        return mUserRequest = mUserRepository.getUser(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password)
                .first()
                .compose(this.<ResponseUser>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {

                        if(responseUser.reason != null && responseUser.reason.isEmpty() && responseUser.user != null){
                            getMvpView().showPinInfo(responseUser.user);
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
            }

            @Override
            public void onNext(ResponseUser response) {

                responseUser = response;
            }
        };
    }

    @Override
    protected boolean showCustomLoading() {
        return true;
    }
}



