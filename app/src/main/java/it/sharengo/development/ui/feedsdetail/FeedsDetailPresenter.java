package it.sharengo.development.ui.feedsdetail;


import it.sharengo.development.data.models.Feed;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class FeedsDetailPresenter extends BasePresenter<FeedsDetailMvpView> {

    private static final String TAG = FeedsDetailPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;

    public Feed feed;

    public FeedsDetailPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
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

    /**
     * Load view for show the information of feed.
     */
    public void loadData(){
        getMvpView().showInformations(feed);
    }

}



