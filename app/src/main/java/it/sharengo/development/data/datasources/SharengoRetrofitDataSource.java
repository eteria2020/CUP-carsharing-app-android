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
    public Observable<Response> getCars(float latitude, float longitude, int radius) {
        return mSharengoApi.getCars(latitude, longitude, radius)
                .compose(this.<Response>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseCar> getCars(String plate) {
        return mSharengoApi.getCars(plate)
                .compose(this.<ResponseCar>handleRetrofitRequest());
    }

    @Override
    public Observable<Response> getPlates() {
        return mSharengoApi.getPlates()
                .compose(this.<Response>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseUser> getUser() {
        return mSharengoApi.getUser()
                .compose(this.<ResponseUser>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseReservation> getReservations() {
        return mSharengoApi.getReservations()
                .compose(this.<ResponseReservation>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponsePutReservation> postReservations(String plate) {
        return mSharengoApi.postReservations(plate)
                .compose(this.<ResponsePutReservation>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponsePutReservation> deleteReservations(int id) {
        return mSharengoApi.deleteReservations(id)
                .compose(this.<ResponsePutReservation>handleRetrofitRequest());
    }

    @Override
    public Observable<ResponseTrip> getTrips(boolean active) {
        return mSharengoApi.getTrips(active)
                .compose(this.<ResponseTrip>handleRetrofitRequest());
    }
}
