package it.sharengo.development.ui.shortintro;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class ShortIntroPresenter extends BasePresenter<ShortIntroMvpView> {

    private static final String TAG = ShortIntroPresenter.class.getSimpleName();

    private final UserRepository mUserRepository;

    public ShortIntroPresenter(SchedulerProvider schedulerProvider, UserRepository userRepository) {
        super(schedulerProvider, userRepository);

        this.mUserRepository = userRepository;
        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



