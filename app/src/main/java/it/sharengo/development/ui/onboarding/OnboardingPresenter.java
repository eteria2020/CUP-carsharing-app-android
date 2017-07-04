package it.sharengo.development.ui.onboarding;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class OnboardingPresenter extends BasePresenter<OnboardingMvpView> {

    private static final String TAG = OnboardingPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;


    public OnboardingPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
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



