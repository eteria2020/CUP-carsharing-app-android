package it.sharengo.development.ui.feeds;


import android.content.Context;
import android.util.Log;

import java.util.List;

import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.FeedCategory;
import it.sharengo.development.data.models.ResponseCity;
import it.sharengo.development.data.models.ResponseFeedCategory;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.CityRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class FeedsPresenter extends BasePresenter<FeedsMvpView> {

    private static final String TAG = FeedsPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final CityRepository mCityRepository;

    private Observable<ResponseFeedCategory> mCategoriesRequest;
    private boolean hideLoading;

    private List<FeedCategory> mCategoriesList;


    public FeedsPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, CityRepository cityRepository) {
        super(schedulerProvider);
        mAppRepository = appRepository;
        mCityRepository = cityRepository;
        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {
        if(mCategoriesList != null) {
            getMvpView().showCategoriesList(mCategoriesList);
        }
    }


    @Override
    protected void subscribeRequestsOnResume() {
        if (mCategoriesRequest != null) {
            addSubscription(mCategoriesRequest.subscribe(getCategoriesSubscriber()));
        }
    }

    @Override
    protected boolean showCustomLoading() {
        if(hideLoading)
            return true;
        else
            return super.showCustomLoading();
    }


    public void loadCategoriesList(Context context) {

        hideLoading = false;

        if( mCategoriesRequest == null) {
            mCategoriesRequest = buildCategoriesRequest(context);
            addSubscription(mCategoriesRequest.unsafeSubscribe(getCategoriesSubscriber()));
        }
    }

    private Observable<ResponseFeedCategory> buildCategoriesRequest(Context context) {
        return mCategoriesRequest = mCityRepository.getCategories(context)
                .first()
                .compose(this.<ResponseFeedCategory>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getMvpView().showCategoriesList(mCategoriesList);
                    }
                });
    }

    private Subscriber<ResponseFeedCategory> getCategoriesSubscriber(){
        return new Subscriber<ResponseFeedCategory>() {
            @Override
            public void onCompleted() {
                mCategoriesRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCategoriesRequest = null;
            }

            @Override
            public void onNext(ResponseFeedCategory response) {
                mCategoriesList = response.data;
            }
        };
    }

}



