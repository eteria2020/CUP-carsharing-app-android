package it.sharengo.development.data.datasources.api;

import java.util.List;

import it.sharengo.development.data.models.Address;
import it.sharengo.development.data.models.Kml;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface KmlApi {


    @GET("zone")
    Observable<Result<Kml>> zone();

    //http://maps.sharengo.it/search.php?q=via+dei+pelaghi&format=json
}
