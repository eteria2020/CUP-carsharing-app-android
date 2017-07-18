package it.sharengo.development.data.datasources;

import it.sharengo.development.data.datasources.api.CitiesApi;
import it.sharengo.development.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.development.data.models.ResponseCity;
import it.sharengo.development.data.models.ResponseFeed;
import it.sharengo.development.data.models.ResponseFeedCategory;
import rx.Observable;

public class CitiesRetrofitDataSource extends BaseRetrofitDataSource implements CitiesDataSource {

    private final CitiesApi mCitiesApi;


    public CitiesRetrofitDataSource(CitiesApi mCitiesApi) {
        this.mCitiesApi = mCitiesApi;
    }


    @Override
    public Observable<ResponseCity> getCities(String auth) {
        return mCitiesApi.getCities(auth)
                .compose(this.<ResponseCity>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseFeedCategory> getCategories(String auth) {
        return mCitiesApi.getCategories(auth)
                .compose(this.<ResponseFeedCategory>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseFeed> getOffers(String auth, String id_category, String id_city) {
        return mCitiesApi.getOffers(auth, id_category, id_city)
                .compose(this.<ResponseFeed>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseFeed> getEvents(String auth, String id_category, String id_city) {
        return mCitiesApi.getEvents(auth, id_category, id_city)
                .compose(this.<ResponseFeed>handleRetrofitRequest());
    }
}
