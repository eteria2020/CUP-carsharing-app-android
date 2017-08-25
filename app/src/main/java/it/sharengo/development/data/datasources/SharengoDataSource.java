package it.sharengo.development.data.datasources;

import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseCar;
import it.sharengo.development.data.models.ResponsePutReservation;
import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import rx.Observable;

public interface SharengoDataSource {

    Observable<Response> getCars(String auth, float latitude, float longitude, int radius);

    Observable<Response> getCars(String auth, float latitude, float longitude, float user_lat, float user_lon, int radius);

    Observable<ResponseCar> getCars(String auth, String plate);

    Observable<ResponseCar> openCars(String auth, String plate, String action);

    Observable<Response> getPlates(String auth);

    Observable<ResponseUser> getUser(String auth);

    Observable<ResponseReservation> getReservations(String auth);

    Observable<ResponsePutReservation> postReservations(String auth, String plate, float user_lat, float user_lon);

    Observable<ResponsePutReservation> deleteReservations(String auth, int id);

    Observable<ResponseTrip> getTrips(String auth, boolean active);

}
