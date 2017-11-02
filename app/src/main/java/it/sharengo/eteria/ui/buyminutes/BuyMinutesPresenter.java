package it.sharengo.eteria.ui.buyminutes;


import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

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



