package it.sharengo.eteria.ui.userarea;


import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

public class UserAreaPresenter extends BasePresenter<UserAreaMvpView> {

    private static final String TAG = UserAreaPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;


    public UserAreaPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);
        mAppRepository = appRepository;
        mUserRepository = userRepository;
        mAppRepository.selectMenuItem(MenuItem.Section.PROFILE);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



