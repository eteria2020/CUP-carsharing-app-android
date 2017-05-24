package it.sharengo.development.ui.home;

import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class HomePresenter extends BasePresenter<HomeMvpView> {

    public HomePresenter(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);
    }

    @Override
    protected void restoreDataOnConfigurationChange() {

    }

    @Override
    protected void subscribeRequestsOnResume() {

    }
}
