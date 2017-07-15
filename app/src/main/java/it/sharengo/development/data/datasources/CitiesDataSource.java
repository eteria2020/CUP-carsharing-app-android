package it.sharengo.development.data.datasources;

import it.sharengo.development.data.models.ResponseCity;
import it.sharengo.development.data.models.ResponseFeedCategory;
import rx.Observable;

public interface CitiesDataSource {

    Observable<ResponseCity> getCities(String auth);
    Observable<ResponseFeedCategory> getCategories(String auth);

}
