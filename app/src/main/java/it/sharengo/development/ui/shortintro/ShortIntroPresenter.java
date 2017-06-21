package it.sharengo.development.ui.shortintro;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class ShortIntroPresenter extends BasePresenter<ShortIntroMvpView> {

    private static final String TAG = ShortIntroPresenter.class.getSimpleName();


    public ShortIntroPresenter(SchedulerProvider schedulerProvider) {
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



