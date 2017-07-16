package it.sharengo.development.ui.feeds;


import android.content.Context;
import android.util.Log;

import java.util.List;

import it.sharengo.development.data.models.Feed;
import it.sharengo.development.data.models.FeedCategory;
import it.sharengo.development.data.models.ResponseFeed;
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
    private Observable<ResponseFeed> mFeedRequest;
    private boolean hideLoading;

    private List<FeedCategory> mCategoriesList;
    private List<Feed> mOffersList;
    private List<Feed> mEventsList;


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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              LOAD Categories
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void loadCategoriesList(Context context) {

        hideLoading = false;

        if( mCategoriesRequest == null) {
            mCategoriesRequest = buildCategoriesRequest(context);
            addSubscription(mCategoriesRequest.unsafeSubscribe(getCategoriesSubscriber()));
        }
    }

    private Observable<ResponseFeedCategory> buildCategoriesRequest(final Context context) {
        return mCategoriesRequest = mCityRepository.getCategories(context)
                .first()
                .compose(this.<ResponseFeedCategory>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        getMvpView().showCategoriesList(mCategoriesList);
                        loadOffersList(context, "5"); //TODO
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              LOAD Offers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void loadOffersList(Context context, String id_city) {

        hideLoading = false;

        if( mFeedRequest == null) {
            mFeedRequest = buildOffersRequest(context, id_city);
            addSubscription(mFeedRequest.unsafeSubscribe(getOffersSubscriber()));
        }
    }

    private Observable<ResponseFeed> buildOffersRequest(final Context context, final String id_city) {
        return mFeedRequest = mCityRepository.getOffers(context, id_city)
                .first()
                .compose(this.<ResponseFeed>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        loadEventsList(context, id_city);
                    }
                });
    }

    private Subscriber<ResponseFeed> getOffersSubscriber(){
        return new Subscriber<ResponseFeed>() {
            @Override
            public void onCompleted() {
                mFeedRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFeedRequest = null;
            }

            @Override
            public void onNext(ResponseFeed response) {
                mFeedRequest = null;
                mOffersList = response.data;
            }
        };
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              LOAD Events
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void loadEventsList(Context context, String id_city) {

        hideLoading = false;

        if( mFeedRequest == null) {
            mFeedRequest = buildEventsRequest(context, id_city);
            addSubscription(mFeedRequest.unsafeSubscribe(getEventsSubscriber()));
        }
    }

    private Observable<ResponseFeed> buildEventsRequest(Context context, String id_city) {
        return mFeedRequest = mCityRepository.getEvents(context, id_city)
                .first()
                .compose(this.<ResponseFeed>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        //getMvpView().showCategoriesList(mCategoriesList); TODO

                        Log.w("mEventsList",": "+mEventsList.size());
                    }
                });
    }

    private Subscriber<ResponseFeed> getEventsSubscriber(){
        return new Subscriber<ResponseFeed>() {
            @Override
            public void onCompleted() {
                mFeedRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFeedRequest = null;
            }

            @Override
            public void onNext(ResponseFeed response) {
                mEventsList = response.data;
            }
        };
    }
}



