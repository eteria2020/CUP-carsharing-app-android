package it.sharengo.eteria.data.datasources.api;

import java.util.List;

import it.sharengo.eteria.data.models.Address;
import it.sharengo.eteria.data.models.ResponseGooglePlace;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface GoogleApi {


    @GET("place/textsearch/json")
    Observable<Result<ResponseGooglePlace>> searchPlace(@Query("query") String query, @Query("location") String location, @Query("language") String language, @Query("key") String key);

    //https://maps.googleapis.com/maps/api/place/textsearch/json?query=via+roma&language=it&location=45.462089,%209.185929&key=AIzaSyAnVjGP9ZCkSkBVkrX-5SBdmNW9AwE_Gew
}
