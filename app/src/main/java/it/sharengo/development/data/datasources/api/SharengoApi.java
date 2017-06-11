package it.sharengo.development.data.datasources.api;

import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseCar;
import it.sharengo.development.data.models.ResponsePutReservation;
import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SharengoApi {

    /*
    * Car
    */

    @GET("cars")
    Observable<Result<Response>> getCars(@Query("lat") float latitude, @Query("lon") float longitude, @Query("radius") int radius);

    @GET("cars")
    Observable<Result<ResponseCar>> getCars(@Query("plate") String plate);

    @GET("cars")
    Observable<Result<Response>> getPlates();

    @FormUrlEncoded
    @PUT("cars/{plate}")
    Observable<Result<ResponseCar>> openCars(@Path("plate") String plate, @Field("action") String action);


    /*
    * User
    */

    @GET("user")
    Observable<Result<ResponseUser>> getUser();

    /*
    * Reservation
    */
    @GET("reservations")
    Observable<Result<ResponseReservation>> getReservations();

    @FormUrlEncoded
    @POST("reservations")
    Observable<Result<ResponsePutReservation>> postReservations(@Field("plate") String plate);

    @DELETE("reservations/{id}")
    Observable<Result<ResponsePutReservation>> deleteReservations(@Path("id") int id);

    /*
    * Trips
    */
    @GET("trips")
    Observable<Result<ResponseTrip>> getTrips(@Query("active") boolean active);

}
