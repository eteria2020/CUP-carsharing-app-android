package it.sharengo.eteria.data.datasources;

import android.util.Log;

import java.util.List;

import it.sharengo.eteria.data.datasources.api.OsmApi;
import it.sharengo.eteria.data.datasources.api.SharengoApi;
import it.sharengo.eteria.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.eteria.data.models.Config;
import it.sharengo.eteria.data.models.OsmPlace;
import it.sharengo.eteria.data.models.Response;
import it.sharengo.eteria.data.models.ResponseCar;
import it.sharengo.eteria.data.models.ResponsePutReservation;
import it.sharengo.eteria.data.models.ResponseReservation;
import it.sharengo.eteria.data.models.ResponseTrip;
import it.sharengo.eteria.data.models.ResponseUser;
import it.sharengo.eteria.data.models.SharengoResponse;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;

public class SharengoRetrofitDataSource extends BaseRetrofitDataSource implements SharengoDataSource {

    private final SharengoApi mSharengoApi;

    private final OsmApi mOsmApi;

    public SharengoRetrofitDataSource(SharengoApi mSharengoApi,OsmApi mOsmApi) {
        this.mSharengoApi = mSharengoApi;
        this.mOsmApi = mOsmApi;
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
        return mSharengoApi.getCars(auth, latitude, longitude, user_lat!=0?String.valueOf(user_lat):null, user_lon!=0?String.valueOf(user_lon):null, radius)
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
    public Observable<ResponseCar> getCars(String auth, String plate, String user_lat, String user_lon, String callingApp,String email) {
        return mSharengoApi.getCars(auth, plate, user_lat, user_lon, callingApp,email)
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
    public Observable<ResponseCar> openCars(String auth, String plate, String action, float user_lat, float user_lon) {
        return mSharengoApi.openCars(auth, plate, action, user_lat!=0?String.valueOf(user_lat):null, user_lon!=0?String.valueOf(user_lon):null)
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
    public Observable<Response> getPlates(String auth, float user_lat, float user_lon) {
        return mSharengoApi.getPlates(auth, String.valueOf(user_lat), String.valueOf(user_lon))
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
    public Observable<ResponseUser> getUser(String auth, float user_lat, float user_lon) {
        return mSharengoApi.getUser(auth, user_lat!=0?String.valueOf(user_lat):null, user_lon!=0?String.valueOf(user_lon):null)
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
        return mSharengoApi.postReservations(auth, plate, user_lat!=0?String.valueOf(user_lat):null, user_lon!=0?String.valueOf(user_lon):null)
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
    public Observable<ResponsePutReservation> deleteReservations(String auth, int id, float user_lat, float user_lon) {
        return mSharengoApi.deleteReservations(auth, id, user_lat!=0?String.valueOf(user_lat):null, user_lon!=0?String.valueOf(user_lon):null)
                .compose(this.<ResponsePutReservation>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseTrip) for manage API getTrips.
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

    /**
     * Returns an observable object (ResponseTrip) for manage API getTrips.
     *
     * @param   auth    identification credentials
     * @param   active  status of trip
     * @param   quantity quantity
     * @return          response trip observable object
     * @see             Observable<ResponseTrip>
     */
    @Override
    public Observable<ResponseTrip> getTrips(String auth, boolean active, int quantity) {
        return mSharengoApi.getTrips(auth, active,quantity)
                .compose(this.<ResponseTrip>handleRetrofitRequest());
    }

    /**
     * Returns an observable object (ResponseTrip) for manage API getCurrentTrips.
     *
     * @param   auth    identification credentials
     * @return          response trip observable object
     * @see             Observable<ResponseTrip>
     */
    @Override
    public Observable<ResponseTrip> getCurrentTrips(String auth) {
        return mSharengoApi.getCurrentTrips(auth)
                .compose(this.<ResponseTrip>handleRetrofitRequest());
    }

    @Override
    public Observable<SharengoResponse<List<Config>>> getConfig() {
        return mSharengoApi.getConfig()
                .compose(this.<SharengoResponse<List<Config>>>handleRetrofitRequest());
    }

    @Override
    public Observable<List<OsmPlace>> searchPlaceOsm(String query, String search, String polygon, String addressdetails,String countrycode, String dedupe) {
        return mOsmApi.searchPlaceOsm( query,  search,  polygon,  addressdetails,countrycode,dedupe)
                .compose(this.<List<OsmPlace>>handleRetrofitRequest());
    }
}
