package it.sharengo.eteria.ui.assistance;


import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

public class AssistancePresenter extends BasePresenter<AssistanceMvpView> {

    private static final String TAG = AssistancePresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;


    public AssistancePresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider,userRepository);
        mAppRepository = appRepository;
        mUserRepository = userRepository;
        mAppRepository.selectMenuItem(MenuItem.Section.HELP);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    @Override
    protected boolean showCustomLoading() {
        return true;
    }
}



