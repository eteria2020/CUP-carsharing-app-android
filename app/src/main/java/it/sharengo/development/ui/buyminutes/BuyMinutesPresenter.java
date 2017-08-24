package it.sharengo.development.ui.buyminutes;


import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class BuyMinutesPresenter extends BasePresenter<BuyMinutesMvpView> {

    private static final String TAG = BuyMinutesPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;


    public BuyMinutesPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider,userRepository);
        mAppRepository = appRepository;
        mAppRepository.selectMenuItem(MenuItem.Section.BUY);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



