package it.sharengo.development.ui.signup;


import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class SignupPresenter extends BasePresenter<SignupMvpView> {

    private static final String TAG = SignupPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;

    public SignupPresenter(SchedulerProvider schedulerProvider,
                           AppRepository appRepository) {
        super(schedulerProvider);

        mAppRepository = appRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.SIGNUP);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



