package it.sharengo.development.data.datasources;

import it.sharengo.development.data.models.ResponseCity;
import it.sharengo.development.data.models.ResponseFeed;
import it.sharengo.development.data.models.ResponseFeedCategory;
import rx.Observable;

public interface CitiesDataSource {

    Observable<ResponseCity> getCities(String auth);
    Observable<ResponseFeedCategory> getCategories(String auth);
    Observable<ResponseFeed> getOffers(String auth, String id_category, String id_city);
    Observable<ResponseFeed> getEvents(String auth, String id_category, String id_city);

}
