package it.sharengo.development.ui.settings;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private static final String TAG = SettingsPresenter.class.getSimpleName();


    public SettingsPresenter(SchedulerProvider schedulerProvider) {
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



