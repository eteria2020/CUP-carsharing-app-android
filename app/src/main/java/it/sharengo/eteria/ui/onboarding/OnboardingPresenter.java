package it.sharengo.eteria.ui.onboarding;


import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

public class OnboardingPresenter extends BasePresenter<OnboardingMvpView> {

    private static final String TAG = OnboardingPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;


    public OnboardingPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository,
                               UserRepository userRepository) {
        super(schedulerProvider,userRepository);
        mAppRepository = appRepository;
        mUserRepository = userRepository;
        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



