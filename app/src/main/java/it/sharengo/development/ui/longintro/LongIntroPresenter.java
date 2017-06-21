package it.sharengo.development.ui.longintro;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class LongIntroPresenter extends BasePresenter<LongIntroMvpView> {

    private static final String TAG = LongIntroPresenter.class.getSimpleName();


    public LongIntroPresenter(SchedulerProvider schedulerProvider) {
        super(schedulerProvider);

        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

}



