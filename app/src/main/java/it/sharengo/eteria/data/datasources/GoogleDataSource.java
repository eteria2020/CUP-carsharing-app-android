package it.sharengo.eteria.data.datasources;

import it.sharengo.eteria.data.models.ResponseGooglePlace;
import it.sharengo.eteria.data.models.ResponseGoogleRoutes;
import rx.Observable;

public interface GoogleDataSource {

    Observable<ResponseGooglePlace> searchPlace(String query, String location, String language, String key);
    Observable<ResponseGoogleRoutes> getRoutes(String origin, String destination, String mode, String key);

}
