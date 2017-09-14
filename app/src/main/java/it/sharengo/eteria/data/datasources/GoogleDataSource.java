package it.sharengo.eteria.data.datasources;

import java.util.List;

import it.sharengo.eteria.data.models.Address;
import it.sharengo.eteria.data.models.ResponseGooglePlace;
import rx.Observable;

public interface GoogleDataSource {

    Observable<ResponseGooglePlace> searchPlace(String query, String location, String language, String key);

}
