package it.sharengo.development.data.datasources;

import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseCar;
import it.sharengo.development.data.models.ResponsePutReservation;
import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.models.User;
import rx.Observable;

public interface SharengoDataSource {

    Observable<Response> getCars(float latitude, float longitude, int radius);

    Observable<ResponseCar> getCars(String plate);

    Observable<Response> getPlates();

    Observable<ResponseUser> getUser();

    Observable<ResponseReservation> getReservations();

    Observable<ResponsePutReservation> postReservations(String plate);

    Observable<ResponseTrip> getTrips(boolean active);

}
