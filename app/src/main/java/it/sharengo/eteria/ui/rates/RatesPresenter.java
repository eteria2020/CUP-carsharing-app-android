package it.sharengo.eteria.ui.rates;


import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.ResponseUser;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class RatesPresenter extends BasePresenter<RatesMvpView> {

    private static final String TAG = RatesPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    private Observable<ResponseUser> mUserRequest;

    private ResponseUser responseUser;

    public RatesPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);
        mAppRepository = appRepository;
        mUserRepository = userRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.RATES);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    public void getRatesInfo(){
        if( mUserRequest == null) {
            mUserRequest = buildUserRequest();
            addSubscription(mUserRequest.unsafeSubscribe(getUserSubscriber()));
        }
    }

    private Observable<ResponseUser> buildUserRequest() {
        return mUserRequest = mUserRepository.getUser(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password, 0, 0)
                .first()
                .compose(this.<ResponseUser>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {

                        if(responseUser.reason != null && responseUser.reason.isEmpty() && responseUser.user != null){
                            getMvpView().showRatesInfo(responseUser.user);
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



