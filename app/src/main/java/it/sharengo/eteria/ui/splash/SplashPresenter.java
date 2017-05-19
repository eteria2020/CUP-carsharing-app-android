package it.sharengo.eteria.ui.splash;

import java.util.concurrent.TimeUnit;

import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Notification;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class SplashPresenter extends BasePresenter<SplashMvpView> {

    private Observable<Object> mSplashRequest;

    public SplashPresenter(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    @Override
    protected void restoreDataOnConfigurationChange() {

    }

    @Override
    protected void subscribeRequestsOnResume() {
        if (mSplashRequest != null) {
            addSubscription(mSplashRequest.subscribe());
        }
    }

    @Override
    protected boolean showCustomLoading() {
        return true;
    }

    public void loadData() {
        if(mSplashRequest == null) {
            mSplashRequest = Observable
                    .just(new Object())
                    .delay(2, TimeUnit.SECONDS)
                    .compose(this.handleDataRequest())
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            getMvpView().navigateToHome();
                        }
                    });

            addSubscription(mSplashRequest.subscribe());
        }
    }
}
