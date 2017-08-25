package it.sharengo.development.data.datasources;

import it.sharengo.development.data.datasources.api.KmlApi;
import it.sharengo.development.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.development.data.models.Kml;
import rx.Observable;

public class KmlRetrofitDataSource extends BaseRetrofitDataSource implements KmlDataSource {

    private final KmlApi mKmlApi;

    public KmlRetrofitDataSource(KmlApi mKmlApi) {
        this.mKmlApi = mKmlApi;
    }

    /**
     * Returns an observable object (Kml) for manage API KML.
     *
     * @return      kml observable object
     * @see         Observable<Kml>
     */
    @Override
    public Observable<Kml> zone() {
        return mKmlApi.zone()
                .compose(this.<Kml>handleRetrofitRequest());
    }
}
