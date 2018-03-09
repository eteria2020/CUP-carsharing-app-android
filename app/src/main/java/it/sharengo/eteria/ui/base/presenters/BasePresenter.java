package it.sharengo.eteria.ui.base.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.ResponseUser;
import it.sharengo.eteria.data.models.UserInfo;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * onResume() and onPause(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public abstract class BasePresenter<T extends MvpView> implements Presenter<T> {

    public static final String TAG = BasePresenter.class.getSimpleName();

    private final SchedulerProvider mSchedulerProvider;
    private final UserRepository mUserRepository;

    private Observable<ResponseUser> mUserRequest;

    private T mMvpView;
    private CompositeSubscription mSubscriptions;
    private int mLoaderDebounce = 150;

    public BasePresenter(SchedulerProvider schedulerProvider, UserRepository userRepository) {
        mSchedulerProvider = schedulerProvider;
        mUserRepository = userRepository;
    }

    @Override
    public void attachView(T mvpView, boolean recreation) {
        mMvpView = mvpView;
        
        if(mSubscriptions == null || mSubscriptions.isUnsubscribed()) {
            mSubscriptions = new CompositeSubscription();
        }

        subscribeRequestsOnResume();

        if(recreation) {
            restoreDataOnConfigurationChange();
        }
    }

    @Override
    public void detachView() {
        mSubscriptions.unsubscribe();
        
        if (isViewAttached()) {
            mMvpView = null;
        }
    }

    @Override
    public boolean isViewAttached() {
        return mMvpView != null;
    }

    abstract protected void subscribeRequestsOnResume();

    abstract protected void restoreDataOnConfigurationChange();

    protected boolean showCustomLoading() {
        return false;
    }

    protected boolean hideCustomLoading() {
        return false;
    }

    protected T getMvpView() {
        return mMvpView;
    }

    protected void addSubscription(Subscription subscription) {
        if(mSubscriptions != null && !mSubscriptions.isUnsubscribed()) {
            mSubscriptions.add(subscription);
        }
        else {
            Log.e(TAG, "You cannot add subscription on null or unsubscribed CompositeSubscription");
        }
    }

    /**
     * Used only for TEST
     * @param mLoaderDebounce loader debounce time
     */
    public void setLoaderDebounce(int mLoaderDebounce) {
        this.mLoaderDebounce = mLoaderDebounce;
    }

    private void manageShowLoading() {
        if (isViewAttached()) {
            if(!showCustomLoading()) {
                getMvpView().showStandardLoading();
            }
        }
    }

    private void manageHideLoading() {
        if (isViewAttached()) {
            if(!hideCustomLoading()) {
                getMvpView().hideStandardLoading();
            }
        }
    }

    protected <T> Observable.Transformer<T, T> handleDataRequest() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(final Observable<T> observable) {
                Observable<T> loader = Observable.just(null);
                return observable
                        .cache()
                        // Comincio con un next nullo che serve per triggerare il loader
                        .startWith(loader)
                        // Ignoro il next nullo se la chiamata risponde entro il tempo di debounce
                        .debounce(mLoaderDebounce, TimeUnit.MILLISECONDS)
                        .observeOn(mSchedulerProvider.ui())
                        .doOnNext(new Action1<T>() {
                            @Override
                            public void call(T t) {
                                if(isViewAttached() && t == null) {
                                    manageShowLoading();
                                }
                            }
                        })
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        })
                        .doOnTerminate(new Action0() {
                            @Override
                            public void call() {
                                manageHideLoading();
                            }
                        })
                        // Filtra gli observable nulli (come quello del loader)
                        .filter(new Func1<T, Boolean>() {
                            @Override
                            public Boolean call(T t) {
                                return t != null;
                            }
                        });
            }
        };
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              User info
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void getUpdateUser(Context context){
        if( mUserRequest == null) {
            mUserRequest = buildUserRequest();
            addSubscription(mUserRequest.unsafeSubscribe(getUserSubscriber(context)));
        }
    }

    private Observable<ResponseUser> buildUserRequest() {


        return mUserRequest = mUserRepository.getUser(mUserRepository.getCachedUser().username, mUserRepository.getCachedUser().password)
                .first()
                .compose(this.<ResponseUser>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        //getReservation();
                    }
                });
    }

    private Subscriber<ResponseUser> getUserSubscriber(final Context context){
        return new Subscriber<ResponseUser>() {
            @Override
            public void onCompleted() {
                mUserRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mUserRequest = null;
                getMvpView().showUserError(e);
            }

            @Override
            public void onNext(ResponseUser response) {

            }
        };
    }

    public boolean isAuth(){
        if(mUserRepository.getCachedUser() != null && !mUserRepository.getCachedUser().username.isEmpty()) return true;
        return false;
    }

    /**
     *
     * @return DisabledType user disabled_type, null otherwise
     */
    public UserInfo.DisabledType getDisabledType(){
        if(mUserRepository.getCachedUser() != null && mUserRepository.getCachedUser().username != null && !mUserRepository.getCachedUser().username.isEmpty()){
            if(!mUserRepository.getCachedUser().userInfo.enabled) {
                return mUserRepository.getCachedUser().userInfo.getDisabledType();
            }else {
                return UserInfo.DisabledType.USER_NOT_DISABLED;
            }
        }else
            return null;
    }

    /**
     *
     * @return DisabledType if user exist end disabled, null otherwise
     */
    public String getDisabledString(Context context){
        if(context == null)
            return "";
        try {

            switch (getDisabledType()) {
                case USER_NOT_DISABLED:
                    return "";
                case FIRST_PAYMENT_NOT_COMPLETED:
                    return context.getString(R.string.first_payment_login_alert);
                case FAILED_PAYMENT:
                    return context.getString(R.string.failed_payment_login_alert);
                case INVALID_DRIVERS_LICENSE:
                    return context.getString(R.string.invalid_driver_license_login_alert);
                case DISABLED_BY_WEBUSER:
                    return context.getString(R.string.disabled_webuser_login_alert);
                case EXPIRED_DRIVERS_LICENSE:
                    return context.getString(R.string.expired_driver_license_login_alert);
                case EXPIRED_CREDIT_CARD:
                    return context.getString(R.string.expired_credit_card_login_alert);
                default:
                    return context.getString(R.string.general_login_alert);
            }
        }catch (Exception e){
            return context.getString(R.string.general_login_alert);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //          LOGOUT
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void logout(Context context, SharedPreferences mPref){

        //mAppRepository.putLang(Locale.getDefault().getLanguage());

        mUserRepository.logoutUser(mPref);
    }

}

