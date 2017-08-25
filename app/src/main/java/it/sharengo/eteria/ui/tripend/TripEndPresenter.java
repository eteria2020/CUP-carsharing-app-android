package it.sharengo.development.ui.tripend;


import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class TripEndPresenter extends BasePresenter<TripEndMvpView> {

    private static final String TAG = TripEndPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;


    public TripEndPresenter(SchedulerProvider schedulerProvider,
                            AppRepository appRepository,
                            UserRepository userRepository) {
        super(schedulerProvider, userRepository);

        mUserRepository = userRepository;
        mAppRepository = appRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.NONE);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



