package it.sharengo.eteria.ui.feedsdetail;


import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

public class FeedsDetailPresenter extends BasePresenter<FeedsDetailMvpView> {

    private static final String TAG = FeedsDetailPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    public Feed feed;

    public FeedsDetailPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
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

    /**
     * Load view for show the information of feed.
     */
    public void loadData(){
        getMvpView().showInformations(feed);
    }

}



