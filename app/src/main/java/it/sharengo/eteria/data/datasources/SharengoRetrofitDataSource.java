package it.sharengo.development.data.datasources;

import it.sharengo.development.data.datasources.api.SharengoApi;
import it.sharengo.development.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseCar;
import it.sharengo.development.data.models.ResponsePutReservation;
import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import rx.Observable;

public class SharengoRetrofitDataSource extends BaseRetrofitDataSource implements SharengoDataSource {

    private final SharengoApi mSharengoApi;


    public SharengoRetrofitDataSource(SharengoApi mSharengoApi) {
        this.mSharengoApi = mSharengoApi;
    }

    /**
     * Returns an observable object (Response) for manage API getCars.
     *
     * @param   auth       identification credentials
     * @param   latitude   latitude to search
     * @param   longitude  longitude to search
     * @param   radius     radius to search
     * @return             response observable object
     * @see                Observable<Response>
     */
    @Override
    public Observable<Response> getCars(String auth, float latitude, float longitude, int radius) {
        return mSharengoApi.getCars(auth, latitude, longitude, radius)
                .compose(this.<Response>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (Response) for manage API getCars; with user location.
     *
     * @param   auth       identification credentials
     * @param   latitude   latitude to search
     * @param   longitude  longitude to search
     * @param   user_lat   latitude of user
     * @param   user_lon   longitude of user
     * @param   radius     radius to search
     * @return             response observable object
     * @see                Observable<Response>
     */
    @Override
    public Observable<Response> getCars(String auth, float latitude, float longitude, float user_lat, float user_lon, int radius) {
        return mSharengoApi.getCars(auth, latitude, longitude, user_lat, user_lon, radius)
                .compose(this.<Response>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseCar) for manage API getCars; with plate to search.
     *
     * @param   auth   identification credentials
     * @param   plate  plate to search
     * @return         response car observable object
     * @see            Observable<ResponseCar>
     */
    @Override
    public Observable<ResponseCar> getCars(String auth, String plate) {
        return mSharengoApi.getCars(auth, plate)
                .compose(this.<ResponseCar>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseCar) for manage API openCars; with plate to search and action to execute.
     *
     * @param   auth    identification credentials
     * @param   plate   plate to search
     * @param   action  action to execute on car
     * @return          response car observable object
     * @see             Observable<ResponseCar>
     */
    @Override
    public Observable<ResponseCar> openCars(String auth, String plate, String action) {
        return mSharengoApi.openCars(auth, plate, action)
                .compose(this.<ResponseCar>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (Response) for manage API getPlates.
     *
     * @param   auth   identification credentials
     * @return         response observable object
     * @see            Observable<Response>
     */
    @Override
    public Observable<Response> getPlates(String auth) {
        return mSharengoApi.getPlates(auth)
                .compose(this.<Response>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseUser) for manage API getUser.
     *
     * @param   auth   identification credentials
     * @return         response observable object
     * @see            Observable<ResponseUser>
     */
    @Override
    public Observable<ResponseUser> getUser(String auth) {
        return mSharengoApi.getUser(auth)
                .compose(this.<ResponseUser>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseReservations) for manage API getReservations.
     *
     * @param   auth   identification credentials
     * @return         response observable object
     * @see            Observable<ResponseReservation>
     */
    @Override
    public Observable<ResponseReservation> getReservations(String auth) {
        return mSharengoApi.getReservations(auth)
                .compose(this.<ResponseReservation>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponsePutReservations) for manage API postReservations; with user location.
     *
     * @param   auth      identification credentials
     * @param   plate     plate to search
     * @param   user_lat  latitude of user
     * @param   user_lon  longitude of user
     * @return            response put reservation observable object
     * @see               Observable<ResponsePutReservation>
     */
    @Override
    public Observable<ResponsePutReservation> postReservations(String auth, String plate, float user_lat, float user_lon) {
        return mSharengoApi.postReservations(auth, plate, user_lat, user_lon)
                .compose(this.<ResponsePutReservation>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponsePutReservations) for manage API deleteReservations; with id of reservation.
     *
     * @param   auth  identification credentials
     * @param   id    plate to search
     * @return        response put reservation observable object
     * @see           Observable<ResponsePutReservation>
     */
    @Override
    public Observable<ResponsePutReservation> deleteReservations(String auth, int id) {
        return mSharengoApi.deleteReservations(auth, id)
                .compose(this.<ResponsePutReservation>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponsePutReservations) for manage API getTrips.
     *
     * @param   auth    identification credentials
     * @param   active  status of trip
     * @return          response trip observable object
     * @see             Observable<ResponseTrip>
     */
    @Override
    public Observable<ResponseTrip> getTrips(String auth, boolean active) {
        return mSharengoApi.getTrips(auth, active)
                .compose(this.<ResponseTrip>handleRetrofitRequest());
    }
}
