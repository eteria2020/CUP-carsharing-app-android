package it.sharengo.development.ui.login;


import android.content.SharedPreferences;
import android.util.Log;

import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.PreferencesRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class LoginPresenter extends BasePresenter<LoginMvpView> {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private final UserRepository mUserRepository;
    private final PreferencesRepository mPreferencesRepository;

    /*
     *  REQUEST
     */
    private Observable<ResponseUser> mUserRequest;


    public LoginPresenter(SchedulerProvider schedulerProvider,
                          UserRepository userRepository,
                          PreferencesRepository preferencesRepository) {
        super(schedulerProvider);

        mUserRepository = userRepository;
        mPreferencesRepository = preferencesRepository;
        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    public void showError(Throwable throwable){
        getMvpView().showError(throwable);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Login
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void login(String username, String password){
        if( mUserRequest == null) {
            mUserRequest = buildUserRequest(username, password);
            addSubscription(mUserRequest.unsafeSubscribe(getUserSubscriber(username, password)));
        }
    }

    private Observable<ResponseUser> buildUserRequest(String username, String password) {
        return mUserRequest = mUserRepository.getUser(username, password)
                .first()
                .compose(this.<ResponseUser>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                    }
                });
    }

    private Subscriber<ResponseUser> getUserSubscriber(final String username, final String password){
        return new Subscriber<ResponseUser>() {
            @Override
            public void onCompleted() {
                mUserRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mUserRequest = null;
                getMvpView().showLoginError(e);
            }

            @Override
            public void onNext(ResponseUser response) {
                //Salvo le credenziali dell'utente
                getMvpView().loginCompleted(username, password);
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              SAVE CREDENTIALS
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void saveCredentials(String username, String password, SharedPreferences mPref){
        mPreferencesRepository.saveCredentials(username, password, mPref);
    }

}



