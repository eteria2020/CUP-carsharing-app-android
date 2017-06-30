package it.sharengo.development.ui.settings;


import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private static final String TAG = SettingsPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;

    public SettingsPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
        super(schedulerProvider);

        mAppRepository = appRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.SETTINGS);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



