package it.sharengo.eteria.ui.home;

import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

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
