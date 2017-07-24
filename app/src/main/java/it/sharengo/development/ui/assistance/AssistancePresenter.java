package it.sharengo.development.ui.assistance;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class AssistancePresenter extends BasePresenter<AssistanceMvpView> {

    private static final String TAG = AssistancePresenter.class.getSimpleName();

    private final AppRepository mAppRepository;


    public AssistancePresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
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



