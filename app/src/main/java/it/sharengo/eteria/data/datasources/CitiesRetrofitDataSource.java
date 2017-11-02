package it.sharengo.eteria.data.datasources;

import it.sharengo.eteria.data.datasources.api.CitiesApi;
import it.sharengo.eteria.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.eteria.data.models.ResponseCity;
import it.sharengo.eteria.data.models.ResponseFeed;
import it.sharengo.eteria.data.models.ResponseFeedCategory;
import rx.Observable;

public class CitiesRetrofitDataSource extends BaseRetrofitDataSource implements CitiesDataSource {

    private final CitiesApi mCitiesApi;


    public CitiesRetrofitDataSource(CitiesApi mCitiesApi) {
        this.mCitiesApi = mCitiesApi;
    }

    /**
     * Returns an observable object (ResponseCity) for manage API getCities.
     *
     * @param   auth  identification credentials
     * @return        kml observable object
     * @see           Observable<ResponseCity>
     */
    @Override
    public Observable<ResponseCity> getCities(String auth) {
        return mCitiesApi.getCities(auth)
                .compose(this.<ResponseCity>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseFeedCategory) for manage API getCategories.
     *
     * @param   auth  identification credentials
     * @return        response feed category observable object
     * @see           Observable<ResponseFeedCategory>
     */
    @Override
    public Observable<ResponseFeedCategory> getCategories(String auth) {
        return mCitiesApi.getCategories(auth)
                .compose(this.<ResponseFeedCategory>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseFeed) for manage API getOffers.
     *
     * @param   auth         identification credentials
     * @param   id_category  id of category
     * @param   id_city      id of city
     * @return               response feed observable object
     * @see                  Observable<ResponseFeed>
     */
    @Override
    public Observable<ResponseFeed> getOffers(String auth, String id_category, String id_city) {
        return mCitiesApi.getOffers(auth, id_category, id_city)
                .compose(this.<ResponseFeed>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseFeed) for manage API getOffersByCoordinates.
     *
     * @param   auth       identification credentials
     * @param   latitude   latitude to search
     * @param   longitude  longitude to search
     * @param   radius     radius to search
     * @return             response feed observable object
     * @see                Observable<ResponseFeed>
     */
    @Override
    public Observable<ResponseFeed> getOffersByCoordinates(String auth, Float latitude, Float longitude, int radius) {
        return mCitiesApi.getOffersByCoordinates(auth, latitude, longitude, radius)
                .compose(this.<ResponseFeed>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseFeed) for manage API getEvents.
     *
     * @param   auth         identification credentials
     * @param   id_category  id of category
     * @param   id_city      id of city
     * @return               response feed observable object
     * @see                  Observable<ResponseFeed>
     */
    @Override
    public Observable<ResponseFeed> getEvents(String auth, String id_category, String id_city) {
        return mCitiesApi.getEvents(auth, id_category, id_city)
                .compose(this.<ResponseFeed>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseFeed) for manage API getEventsByCoordinates.
     *
     * @param   auth       identification credentials
     * @param   latitude   latitude to search
     * @param   longitude  longitude to search
     * @param   radius     radius to search
     * @return             response feed observable object
     * @see                Observable<ResponseFeed>
     */
    @Override
    public Observable<ResponseFeed> getEventsByCoordinates(String auth, Float latitude, Float longitude, int radius) {
        return mCitiesApi.getEventsByCoordinates(auth, latitude, longitude, radius)
                .compose(this.<ResponseFeed>handleRetrofitRequest());
    }
}
