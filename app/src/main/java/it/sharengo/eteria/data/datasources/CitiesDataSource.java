package it.sharengo.eteria.data.datasources;

import it.sharengo.eteria.data.models.ResponseCity;
import it.sharengo.eteria.data.models.ResponseFeed;
import it.sharengo.eteria.data.models.ResponseFeedCategory;
import rx.Observable;

public interface CitiesDataSource {

    Observable<ResponseCity> getCities(String auth);
    Observable<ResponseFeedCategory> getCategories(String auth);
    Observable<ResponseFeed> getOffers(String auth, String id_category, String id_city);
    Observable<ResponseFeed> getOffersByCoordinates(String auth, Float latitude, Float longitude, int radius);
    Observable<ResponseFeed> getEvents(String auth, String id_category, String id_city);
    Observable<ResponseFeed> getEventsByCoordinates(String auth, Float latitude, Float longitude, int radius);

}
