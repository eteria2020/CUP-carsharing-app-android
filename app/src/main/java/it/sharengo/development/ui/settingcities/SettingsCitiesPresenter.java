package it.sharengo.development.ui.settingcities;


import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class SettingsCitiesPresenter extends BasePresenter<SettingsCitiesMvpView> {

    private static final String TAG = SettingsCitiesPresenter.class.getSimpleName();


    public SettingsCitiesPresenter(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);

        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



