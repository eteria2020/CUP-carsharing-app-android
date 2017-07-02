package it.sharengo.development.ui.settingsaddressesnew;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class SettingsAddressesNewPresenter extends BasePresenter<SettingsAddressesNewMvpView> {

    private static final String TAG = SettingsAddressesNewPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;


    public SettingsAddressesNewPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
        super(schedulerProvider);
        mAppRepository = appRepository;
        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



