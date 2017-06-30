package it.sharengo.development.ui.settingsaddresses;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class SettingsAddressesPresenter extends BasePresenter<SettingsAddressesMvpView> {

    private static final String TAG = SettingsAddressesPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;


    public SettingsAddressesPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
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



