package it.sharengo.eteria.ui.settings;


import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

public class SettingsPresenter extends BasePresenter<SettingsMvpView> {

    private static final String TAG = SettingsPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    public SettingsPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);

        mAppRepository = appRepository;
        this.mUserRepository = userRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.SETTINGS);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



