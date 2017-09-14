package it.sharengo.eteria.ui.feeds;


import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.data.models.FeedCategory;
import it.sharengo.eteria.data.models.ResponseFeed;
import it.sharengo.eteria.data.models.ResponseFeedCategory;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.CityRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.base.presenters.BasePresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class FeedsPresenter extends BasePresenter<FeedsMvpView> {

    private static final String TAG = FeedsPresenter.class.getSimpleName();

    private final AppRepository mAppRepository;
    private final CityRepository mCityRepository;
    private final UserRepository mUserRepository;

    private Observable<ResponseFeedCategory> mCategoriesRequest;
    private Observable<ResponseFeed> mFeedRequest;
    private boolean hideLoading;
    public String city_id;
    public String category_id;
    public String category_name;

    private List<FeedCategory> mCategoriesList;
    private List<Feed> mOffersList;
    private List<Feed> mEventsList;


    public FeedsPresenter(SchedulerProvider schedulerProvider, AppRepository appRepository, CityRepository cityRepository, UserRepository userRepository) {
        super(schedulerProvider,userRepository);
        mAppRepository = appRepository;
        mCityRepository = cityRepository;
        mUserRepository = userRepository;
        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

        if(mCategoriesList != null) {
            getMvpView().showCategoriesList(mCategoriesList);
        }
        if(mOffersList != null && mEventsList != null) {
            setAllFeedsList();
        }
    }


    @Override
    protected void subscribeRequestsOnResume() {
        if (mCategoriesRequest != null) {
            addSubscription(mCategoriesRequest.subscribe(getCategoriesSubscriber()));
        }
        if(mOffersList != null && mCategoriesList != null)
            setAllFeedsList();
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

    /**
     * Load from server categories list.
     *
     * @param  context  context of application.
     */
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
                        loadOffersList(context, city_id);
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

    /**
     * Load from server the offers available for city passing as param.
     *
     * @param  context  context of application.
     * @param  id_city id of city necessary for retrieve offers.
     */
    public void loadOffersList(Context context, String id_city) {

        hideLoading = true;

        if( mFeedRequest == null) {
            mFeedRequest = buildOffersRequest(context, id_city);
            addSubscription(mFeedRequest.unsafeSubscribe(getOffersSubscriber()));
        }
    }

    private Observable<ResponseFeed> buildOffersRequest(final Context context, final String id_city) {
        return mFeedRequest = mCityRepository.getOffers(context, category_id, id_city)
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
    /**
     * Load from server the events available for city passing as param.
     *
     * @param  context  context of application.
     * @param  id_city id of city necessary for retrieve events.
     */
    public void loadEventsList(Context context, String id_city) {

        hideLoading = true;

        if( mFeedRequest == null) {
            mFeedRequest = buildEventsRequest(context, id_city);
            addSubscription(mFeedRequest.unsafeSubscribe(getEventsSubscriber()));
        }
    }

    private Observable<ResponseFeed> buildEventsRequest(Context context, String id_city) {
        return mFeedRequest = mCityRepository.getEvents(context, category_id, id_city)
                .first()
                .compose(this.<ResponseFeed>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        setAllFeedsList();
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

    private void setAllFeedsList(){

        //Verifico se ci sono evento o offerte
        if(mEventsList.isEmpty() && mOffersList.isEmpty()){

            //Schermata vuota
            getMvpView().showEmptyMessage();

        }else {
            List<Feed> feeds = new ArrayList<>();

            //Ordino gli eventi
            Collections.sort(mEventsList, new Comparator<Feed>() {
                public int compare(Feed o1, Feed o2) {

                    return convertDate(o1.informations.date.date).compareTo(convertDate(o2.informations.date.date));
                }
            });
            //Ordino le offerte
            Collections.sort(mOffersList, new Comparator<Feed>() {
                public int compare(Feed o1, Feed o2) {

                    return convertDate(o1.informations.date.date).compareTo(convertDate(o2.informations.date.date));
                }
            });

            //Concateno offerte + eventi
            feeds.addAll(mOffersList);
            feeds.addAll(mEventsList);

            getMvpView().showAllFeedsList(feeds);
        }
    }

    private Date convertDate(String dateString){ //"31-07-2017 23:59"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        Date convertedDate = new Date();

        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
        }

        return convertedDate;
    }
}



