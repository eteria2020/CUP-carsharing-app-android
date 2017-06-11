package it.sharengo.development.data.datasources.api;

import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseUser;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface SharengoApi {

    /*
    * Car
    */

    @GET("cars")
    Observable<Result<Response>> getCars(@Query("lat") float latitude, @Query("lon") float longitude, @Query("radius") int radius);

    @GET("cars")
    Observable<Result<Response>> getPlates();


    /*
    * User
    */

    @GET("user")
    Observable<Result<ResponseUser>> getUser();

}
