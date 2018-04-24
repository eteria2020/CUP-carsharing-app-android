package it.sharengo.eteria.data.datasources.api;

import it.sharengo.eteria.data.models.Response;
import it.sharengo.eteria.data.models.ResponseCar;
import it.sharengo.eteria.data.models.ResponsePutReservation;
import it.sharengo.eteria.data.models.ResponseReservation;
import it.sharengo.eteria.data.models.ResponseTrip;
import it.sharengo.eteria.data.models.ResponseUser;
import retrofit2.adapter.rxjava.Result;
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
    @GET("v3/cars")
    Observable<Result<Response>> getCars(@Header("Authorization") String auth, @Query("lat") float latitude, @Query("lon") float longitude, @Query("radius") int radius);

    @GET("v3/cars")
    Observable<Result<Response>> getCars(@Header("Authorization") String auth, @Query("lat") float latitude, @Query("lon") float longitude, @Query("user_lat") String user_lat, @Query("user_lon") String user_lon, @Query("radius") int radius);

    @GET("v3/cars")
    Observable<Result<ResponseCar>> getCars(@Header("Authorization") String auth, @Query("plate") String plate, @Query("user_lat") String user_lat, @Query("user_lon") String user_lon, @Query("callingApp") String callingApp);

    @GET("v3/cars")
    Observable<Result<Response>> getPlates(@Header("Authorization") String auth, @Query("user_lat") String user_lat, @Query("user_lon") String user_lon);

    @FormUrlEncoded
    @PUT("v2/cars/{plate}")
    Observable<Result<ResponseCar>> openCars(@Header("Authorization") String auth, @Path("plate") String plate, @Field("action") String action, @Query("user_lat") String user_lat, @Query("user_lon") String user_lon);


    /*
    * User
    */

    @GET("v3/user")
    Observable<Result<ResponseUser>> getUser(@Header("Authorization") String auth, @Query("user_lat") String user_lat, @Query("user_lon") String user_lon);

    /*
    * Reservation
    */
    @GET("v2/reservations")
    Observable<Result<ResponseReservation>> getReservations(@Header("Authorization") String auth);

    @FormUrlEncoded
    @POST("v2/reservations")
    Observable<Result<ResponsePutReservation>> postReservations(@Header("Authorization") String auth, @Field("plate") String plate, @Field("user_lat") String user_lat, @Field("user_lon") String user_lon);

    @DELETE("v2/reservations/{id}")
    Observable<Result<ResponsePutReservation>> deleteReservations(@Header("Authorization") String auth, @Path("id") int id, @Query("user_lat") String user_lat, @Query("user_lon") String user_lon);

    /*
    * Trips
    */
    @GET("v3/trips")
    Observable<Result<ResponseTrip>> getTrips(@Header("Authorization") String auth, @Query("active") boolean active);

    @GET("v3/trips")
    Observable<Result<ResponseTrip>> getTrips(@Header("Authorization") String auth, @Query("active") boolean active, @Query("quantity") int quantity);

    @GET("v2/trips/current")
    Observable<Result<ResponseTrip>> getCurrentTrips(@Header("Authorization") String auth);



}
