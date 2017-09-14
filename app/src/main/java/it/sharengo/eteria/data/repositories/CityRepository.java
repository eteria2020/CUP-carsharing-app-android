package it.sharengo.eteria.data.repositories;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.eteria.R;
import it.sharengo.eteria.data.datasources.CitiesDataSource;
import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.data.models.ResponseFeed;
import it.sharengo.eteria.data.models.ResponseFeedCategory;
import okhttp3.Credentials;
import rx.Observable;
import rx.functions.Action1;

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

    /**
     * Invoke API getCategories for retrieve data.
     *
     * @param  context  context of application
     * @return          response feed category observable object
     * @see             Observable<ResponseFeedCategory>
     */
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

    /**
     * Invoke API getOffers with params received from app.
     *
     * @param  context      context of application
     * @param  id_category  id of category
     * @param  id_city      id of city
     * @return              response feed observable object
     * @see                 Observable<ResponseFeed>
     */
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
    //                                              GET Feed Offers by Coordinates
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API getOffersByCoordinates with params received from app.
     *
     * @param  context    context of application
     * @param  latitude   latitude to search
     * @param  longitude  longitude to search
     * @return            response feed observable object
     * @see               Observable<ResponseFeed>
     */
    public Observable<ResponseFeed> getOffersByCoordinates(final Context context, Float latitude, Float longitude, int radius) {

        return mRemoteDataSource.getOffersByCoordinates(Credentials.basic(context.getString(R.string.endpointCitiesUser), context.getString(R.string.endpointCitiesPass)), latitude, longitude, radius)
                .doOnNext(new Action1<ResponseFeed>() {
                    @Override
                    public void call(ResponseFeed response) {

                        createOrUpdateOffersByCoordinatesInMemory(context, response);
                    }
                });
    }

    private void createOrUpdateOffersByCoordinatesInMemory(Context context, ResponseFeed response) {
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

    /**
     * Invoke API getEvents with params received from app.
     *
     * @param  context      context of application
     * @param  id_category  id of category
     * @param  id_city      id of city
     * @return              response feed observable object
     * @see                 Observable<ResponseFeed>
     */
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Feed Events by Coordinates
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API getEventsByCoordinates with params received from app.
     *
     * @param  context    context of application
     * @param  latitude   latitude to search
     * @param  longitude  longitude to search
     * @param  radius     radius to search
     * @return            response feed observable object
     * @see               Observable<ResponseFeed>
     */
    public Observable<ResponseFeed> getEventsByCoordinates(final Context context, Float latitude, Float longitude, int radius) {

        return mRemoteDataSource.getEventsByCoordinates(Credentials.basic(context.getString(R.string.endpointCitiesUser), context.getString(R.string.endpointCitiesPass)), latitude, longitude, radius)
                .doOnNext(new Action1<ResponseFeed>() {
                    @Override
                    public void call(ResponseFeed response) {

                        createOrUpdateEventsByCoordinatesInMemory(context, response);
                    }
                });
    }

    private void createOrUpdateEventsByCoordinatesInMemory(Context context, ResponseFeed response) {
        if (mChachedEvents == null) {
            mChachedEvents = new ArrayList<Feed>();
        }
        mChachedEvents = response.data;
    }
}
