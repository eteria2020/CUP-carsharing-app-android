package it.sharengo.development.data.datasources.api;

import java.util.List;

import it.sharengo.development.data.models.Address;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface SharengoMapApi {


    @GET("search.php")
    Observable<Result<List<Address>>> searchAddress(@Query("q") String address, @Query("format") String format);

    //http://maps.sharengo.it/search.php?q=via+dei+pelaghi&format=json
}
