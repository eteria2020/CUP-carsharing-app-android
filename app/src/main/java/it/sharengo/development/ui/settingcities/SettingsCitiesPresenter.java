package it.sharengo.development.ui.settingcities;


import android.content.Context;
import android.util.Log;

import java.util.List;

import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.models.ResponseCity;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class SettingsCitiesPresenter extends BasePresenter<SettingsCitiesMvpView> {

    private static final String TAG = SettingsCitiesPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;

    private Observable<ResponseCity> mCityRequest;

    private List<City> mCitiesList;

    public SettingsCitiesPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository) {
        super(schedulerProvider);

        mAppRepository = appRepository;

        mAppRepository.selectMenuItem(MenuItem.Section.SETTINGS);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {
        if(mCitiesList != null) {
            getMvpView().showList(mCitiesList);
        }
    }


    @Override
    protected void subscribeRequestsOnResume() {
        if (mCityRequest != null) {
            addSubscription(mCityRequest.subscribe(getCitiesSubscriber()));
        }
    }


    public void loadList(Context context) {

        if( mCityRequest == null) {
            mCityRequest = buildCitiesRequest(context);
            addSubscription(mCityRequest.unsafeSubscribe(getCitiesSubscriber()));
        }
    }

    private Observable<ResponseCity> buildCitiesRequest(Context context) {
        return mCityRequest = mAppRepository.getCities(context)
                .first()
                .compose(this.<ResponseCity>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getMvpView().showList(mCitiesList);
                    }
                });
    }

    private Subscriber<ResponseCity> getCitiesSubscriber(){
        return new Subscriber<ResponseCity>() {
            @Override
            public void onCompleted() {
                mCityRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCityRequest = null;
            }

            @Override
            public void onNext(ResponseCity response) {
                mCitiesList = response.data;
            }
        };
    }
}



