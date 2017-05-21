package it.sharengo.eteria.ui.map;


import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

public class MapPresenter extends BasePresenter<MapMvpView> {

    private static final String TAG = MapPresenter.class.getSimpleName();



    public MapPresenter(SchedulerProvider schedulerProvider) {
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



