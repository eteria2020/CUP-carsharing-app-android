package it.sharengo.eteria.data.datasources;

import it.sharengo.eteria.data.models.Response;
import it.sharengo.eteria.data.models.ResponseCar;
import it.sharengo.eteria.data.models.ResponsePutReservation;
import it.sharengo.eteria.data.models.ResponseReservation;
import it.sharengo.eteria.data.models.ResponseTrip;
import it.sharengo.eteria.data.models.ResponseUser;
import rx.Observable;

public interface SharengoDataSource {

    Observable<Response> getCars(String auth, float latitude, float longitude, int radius);

    Observable<Response> getCars(String auth, float latitude, float longitude, float user_lat, float user_lon, int radius);

    Observable<ResponseCar> getCars(String auth, String plate, String user_lat, String user_lon, String callingApp);

    Observable<ResponseCar> openCars(String auth, String plate, String action, float user_lat, float user_lon);

    Observable<Response> getPlates(String auth, float user_lat, float user_lon);

    Observable<ResponseUser> getUser(String auth, float user_lat, float user_lon);

    Observable<ResponseReservation> getReservations(String auth);

    Observable<ResponsePutReservation> postReservations(String auth, String plate, float user_lat, float user_lon);

    Observable<ResponsePutReservation> deleteReservations(String auth, int id, float user_lat, float user_lon);

    Observable<ResponseTrip> getTrips(String auth, boolean active);

    Observable<ResponseTrip> getTrips(String auth, boolean active, int quantity);

    Observable<ResponseTrip> getCurrentTrips(String auth);

}
