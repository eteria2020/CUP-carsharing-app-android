package it.sharengo.development.ui.mapgoogle;


import android.util.Log;

import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.map.BaseMapPresenter;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

public class MapGooglePresenter extends BaseMapPresenter<MapGoogleMvpView> {

    private static final String TAG = MapGooglePresenter.class.getSimpleName();

    private Boolean mMapIsReady = false;
    private Boolean mLocationIsReady = false;
    private Double mLatitude;
    private Double mLongitude;

    private final AppRepository mAppRepository;


    public MapGooglePresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
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

    public void onMapIsReady() {
        mMapIsReady = true;
        Log.w("onMapIsReady","YES");
    }

    public void onLocationIsReady(Double lat, Double lng) {
        mLocationIsReady = true;
        mLatitude = lat;
        mLongitude = lng;
        Log.w("onLocationIsReady","YES");
    }

}



