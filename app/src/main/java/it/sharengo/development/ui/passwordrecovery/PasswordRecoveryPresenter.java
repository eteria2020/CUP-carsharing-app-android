package it.sharengo.development.ui.passwordrecovery;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class PasswordRecoveryPresenter extends BasePresenter<PasswordRecoveryMvpView> {

    private static final String TAG = PasswordRecoveryPresenter.class.getSimpleName();


    public PasswordRecoveryPresenter(SchedulerProvider schedulerProvider) {
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



