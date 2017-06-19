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
import retrofit2.http.Header;
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
    Observable<Result<Response>> getCars(@Header("Authorization") String auth, @Query("lat") float latitude, @Query("lon") float longitude, @Query("radius") int radius);

    @GET("cars")
    Observable<Result<ResponseCar>> getCars(@Header("Authorization") String auth, @Query("plate") String plate);

    @GET("cars")
    Observable<Result<Response>> getPlates(@Header("Authorization") String auth);

    @FormUrlEncoded
    @PUT("cars/{plate}")
    Observable<Result<ResponseCar>> openCars(@Header("Authorization") String auth, @Path("plate") String plate, @Field("action") String action);


    /*
    * User
    */

    @GET("user")
    Observable<Result<ResponseUser>> getUser(@Header("Authorization") String auth);

    /*
    * Reservation
    */
    @GET("reservations")
    Observable<Result<ResponseReservation>> getReservations(@Header("Authorization") String auth);

    @FormUrlEncoded
    @POST("reservations")
    Observable<Result<ResponsePutReservation>> postReservations(@Header("Authorization") String auth, @Field("plate") String plate);

    @DELETE("reservations/{id}")
    Observable<Result<ResponsePutReservation>> deleteReservations(@Header("Authorization") String auth, @Path("id") int id);

    /*
    * Trips
    */
    @GET("trips")
    Observable<Result<ResponseTrip>> getTrips(@Header("Authorization") String auth, @Query("active") boolean active);

}
