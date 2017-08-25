package it.sharengo.development.ui.slideshow;


import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class SlideshowPresenter extends BasePresenter<SlideshowMvpView> {

    private static final String TAG = SlideshowPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    public SlideshowPresenter(SchedulerProvider schedulerProvider,
                              AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider,userRepository);

        mAppRepository = appRepository;
        this.mUserRepository = userRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.SIGNUP);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



