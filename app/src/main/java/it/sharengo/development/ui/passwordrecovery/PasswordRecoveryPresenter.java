package it.sharengo.development.ui.passwordrecovery;


import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class PasswordRecoveryPresenter extends BasePresenter<PasswordRecoveryMvpView> {

    private static final String TAG = PasswordRecoveryPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;


    public PasswordRecoveryPresenter(SchedulerProvider schedulerProvider,
                                     AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);

        mAppRepository = appRepository;
        this.mUserRepository = userRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.NONE);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



