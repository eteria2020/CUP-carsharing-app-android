package it.sharengo.eteria.ui.settingcities;


import android.content.Context;

import java.util.List;

import it.sharengo.eteria.data.models.City;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.data.models.ResponseCity;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class SettingsCitiesPresenter extends BasePresenter<SettingsCitiesMvpView> {

    private static final String TAG = SettingsCitiesPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final UserRepository mUserRepository;

    private Observable<ResponseCity> mCityRequest;
    private boolean hideLoading;

    private List<City> mCitiesList;

    public SettingsCitiesPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        super(schedulerProvider, userRepository);

        mAppRepository = appRepository;
        this.mUserRepository = userRepository;

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

    @Override
    protected boolean showCustomLoading() {
        if(hideLoading)
            return true;
        else
            return super.showCustomLoading();
    }

    /**
     * Load list of city from server.
     *
     * @param  context  context of application
     */
    public void loadList(Context context) {

        hideLoading = true;

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



