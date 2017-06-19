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


    @Override
    public Observable<Response> getCars(String auth, float latitude, float longitude, int radius) {
        return mSharengoApi.getCars(auth, latitude, longitude, radius)
                .compose(this.<Response>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseCar> getCars(String auth, String plate) {
        return mSharengoApi.getCars(auth, plate)
                .compose(this.<ResponseCar>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseCar> openCars(String auth, String plate, String action) {
        return mSharengoApi.openCars(auth, plate, action)
                .compose(this.<ResponseCar>handleRetrofitRequest());
    }

    @Override
    public Observable<Response> getPlates(String auth) {
        return mSharengoApi.getPlates(auth)
                .compose(this.<Response>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseUser> getUser(String auth) {
        return mSharengoApi.getUser(auth)
                .compose(this.<ResponseUser>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseReservation> getReservations(String auth) {
        return mSharengoApi.getReservations(auth)
                .compose(this.<ResponseReservation>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponsePutReservation> postReservations(String auth, String plate) {
        return mSharengoApi.postReservations(auth, plate)
                .compose(this.<ResponsePutReservation>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponsePutReservation> deleteReservations(String auth, int id) {
        return mSharengoApi.deleteReservations(auth, id)
                .compose(this.<ResponsePutReservation>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseTrip> getTrips(String auth, boolean active) {
        return mSharengoApi.getTrips(auth, active)
                .compose(this.<ResponseTrip>handleRetrofitRequest());
    }
}
