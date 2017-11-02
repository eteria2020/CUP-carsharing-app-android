package it.sharengo.eteria.ui.longintro;


import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

public class LongIntroPresenter extends BasePresenter<LongIntroMvpView> {

    private static final String TAG = LongIntroPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    public LongIntroPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);

        mAppRepository = appRepository;
        this.mUserRepository = userRepository;

        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }

    public void onCreateView(){
        mAppRepository.setAnimateHome(false);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}


