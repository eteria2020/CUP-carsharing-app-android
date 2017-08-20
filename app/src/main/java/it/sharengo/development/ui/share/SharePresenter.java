package it.sharengo.development.ui.share;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class SharePresenter extends BasePresenter<ShareMvpView> {

    private static final String TAG = SharePresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;



    public SharePresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
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



