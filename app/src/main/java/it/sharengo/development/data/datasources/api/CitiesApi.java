package it.sharengo.development.data.datasources.api;

import it.sharengo.development.data.models.ResponseCity;
import it.sharengo.development.data.models.ResponseFeed;
import it.sharengo.development.data.models.ResponseFeedCategory;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import rx.Observable;

public interface CitiesApi {

    @GET("feed/cities/list")
    Observable<Result<ResponseCity>> getCities(@Header("Authorization") String auth);

    @GET("feed/categories/list")
    Observable<Result<ResponseFeedCategory>> getCategories(@Header("Authorization") String auth);

    @GET("feed/category/{id_category}/city/{id_city}/offers")
    Observable<Result<ResponseFeed>> getOffers(@Header("Authorization") String auth, @Path("id_category") String id_category, @Path("id_city") String id_city);

    @GET("feed/category/{id_category}/city/{id_city}/events")
    Observable<Result<ResponseFeed>> getEvents(@Header("Authorization") String auth, @Path("id_category") String id_category, @Path("id_city") String id_city);


}
