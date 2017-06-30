package it.sharengo.development.data.datasources.api;

import it.sharengo.development.data.models.ResponseCity;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface CitiesApi {

    @GET("feed/cities/list")
    Observable<Result<ResponseCity>> getCities(@Header("Authorization") String auth);
}
