package it.sharengo.development.ui.longintro;


import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class LongIntroPresenter extends BasePresenter<LongIntroMvpView> {

    private static final String TAG = LongIntroPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;

    public LongIntroPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
        super(schedulerProvider);

        mAppRepository = appRepository;

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



