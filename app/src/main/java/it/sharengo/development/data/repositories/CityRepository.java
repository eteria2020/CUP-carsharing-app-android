package it.sharengo.development.data.repositories;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.R;
import it.sharengo.development.data.datasources.CitiesDataSource;
import it.sharengo.development.data.models.Feed;
import it.sharengo.development.data.models.Post;
import it.sharengo.development.data.models.ResponseFeed;
import it.sharengo.development.data.models.ResponseFeedCategory;
import okhttp3.Credentials;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton
public class CityRepository {

    public static final String TAG = CityRepository.class.getSimpleName();

    private CitiesDataSource mRemoteDataSource;
    private ResponseFeedCategory mChachedCategories;
    private List<Feed> mChachedOffers;
    private List<Feed> mChachedEvents;


    @Inject
    public CityRepository(CitiesDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Feed Categories
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<ResponseFeedCategory> getCategories(final Context context) {

        return mRemoteDataSource.getCategories(Credentials.basic(context.getString(R.string.endpointCitiesUser), context.getString(R.string.endpointCitiesPass)))
                .doOnNext(new Action1<ResponseFeedCategory>() {
                    @Override
                    public void call(ResponseFeedCategory response) {

                        createOrUpdateCategoriesInMemory(context, response);
                    }
                });
    }

    private void createOrUpdateCategoriesInMemory(Context context, ResponseFeedCategory response) {
        if (mChachedCategories == null) {
            mChachedCategories = new ResponseFeedCategory();
        }
        mChachedCategories = response;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Feed Offers
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<ResponseFeed> getOffers(final Context context, String id_category, String id_city) {

        return mRemoteDataSource.getOffers(Credentials.basic(context.getString(R.string.endpointCitiesUser), context.getString(R.string.endpointCitiesPass)), id_category, id_city)
                .doOnNext(new Action1<ResponseFeed>() {
                    @Override
                    public void call(ResponseFeed response) {

                        createOrUpdateOffersInMemory(context, response);
                    }
                });
    }

    private void createOrUpdateOffersInMemory(Context context, ResponseFeed response) {
        if (mChachedOffers == null) {
            mChachedOffers = new ArrayList<Feed>();
        }
        mChachedOffers = response.data;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Feed Events
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<ResponseFeed> getEvents(final Context context, String id_category, String id_city) {

        return mRemoteDataSource.getEvents(Credentials.basic(context.getString(R.string.endpointCitiesUser), context.getString(R.string.endpointCitiesPass)), id_category, id_city)
                .doOnNext(new Action1<ResponseFeed>() {
                    @Override
                    public void call(ResponseFeed response) {

                        createOrUpdateEventsInMemory(context, response);
                    }
                });
    }

    private void createOrUpdateEventsInMemory(Context context, ResponseFeed response) {
        if (mChachedEvents == null) {
            mChachedEvents = new ArrayList<Feed>();
        }
        mChachedEvents = response.data;
    }
}
