package it.sharengo.development.data.datasources.api;

import java.util.List;
import java.util.Map;

import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.Cars;
import it.sharengo.development.data.models.Post;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface SharengoApi {


    @GET("cars")
    Observable<Result<Cars>> getCars(@Query("lat") float latitude, @Query("lon") float longitude, @Query("radius") int radius);

    @GET("cars")
    Observable<Result<Cars>> getPlates();

    //https://api.sharengo.it:8023/v2/cars?lat=45.1456&lon=12.4543&radius=100
}
