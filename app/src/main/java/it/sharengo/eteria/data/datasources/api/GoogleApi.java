package it.sharengo.eteria.data.datasources.api;

import it.sharengo.eteria.data.models.ResponseGooglePlace;
import it.sharengo.eteria.data.models.ResponseGoogleRoutes;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface GoogleApi {


    @GET("place/textsearch/json")
    Observable<Result<ResponseGooglePlace>> searchPlace(@Query("query") String query, @Query("location") String location, @Query("language") String language, @Query("key") String key);

    @GET("directions/json")
    Observable<Result<ResponseGoogleRoutes>> getRoutes(@Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode, @Query("key") String key);

}
