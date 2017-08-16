package it.sharengo.development.data.datasources;

import java.util.List;

import it.sharengo.development.data.datasources.api.KmlApi;
import it.sharengo.development.data.datasources.api.SharengoApi;
import it.sharengo.development.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.development.data.models.Kml;
import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseCar;
import it.sharengo.development.data.models.ResponsePutReservation;
import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import rx.Observable;

public class KmlRetrofitDataSource extends BaseRetrofitDataSource implements KmlDataSource {

    private final KmlApi mKmlApi;


    public KmlRetrofitDataSource(KmlApi mKmlApi) {
        this.mKmlApi = mKmlApi;
    }


    @Override
    public Observable<Kml> zone() {
        return mKmlApi.zone()
                .compose(this.<Kml>handleRetrofitRequest());
    }
}
