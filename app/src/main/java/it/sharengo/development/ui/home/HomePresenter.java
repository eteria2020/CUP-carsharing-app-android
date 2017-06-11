package it.sharengo.development.ui.home;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.models.User;
import it.sharengo.development.data.repositories.AuthRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;

public class HomePresenter extends BasePresenter<HomeMvpView> {

    private final AuthRepository mAuthRepository;
    private final UserRepository mUserRepository;

    /*
     *  REQUEST
     */
    private Observable<ResponseUser> mUserRequest;

    public HomePresenter(SchedulerProvider schedulerProvider,
                         AuthRepository authRepository,
                         UserRepository userRepository) {
        super(schedulerProvider);
        mAuthRepository = authRepository;
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
            mUserRequest = buildPlatesRequest();
            addSubscription(mUserRequest.unsafeSubscribe(getPlatesSubscriber()));
        }
    }

    private Observable<ResponseUser> buildPlatesRequest() {
        return mUserRequest = mUserRepository.getUser()
                .first()
                .compose(this.<ResponseUser>handleDataRequest());
    }

    private Subscriber<ResponseUser> getPlatesSubscriber(){
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
                Log.w("pin",": "+response.user.pin);
                Log.w("status",": "+response.status);
                Log.w("reason",": "+response.reason);
            }
        };
    }
}
