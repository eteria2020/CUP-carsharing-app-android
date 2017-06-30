package it.sharengo.development.data.datasources;

import it.sharengo.development.data.datasources.api.CitiesApi;
import it.sharengo.development.data.datasources.api.SharengoApi;
import it.sharengo.development.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseCity;
import rx.Observable;

import static android.R.attr.radius;

public class CitiesRetrofitDataSource extends BaseRetrofitDataSource implements CitiesDataSource {

    private final CitiesApi mCitiesApi;


    public CitiesRetrofitDataSource(CitiesApi mCitiesApi) {
        this.mCitiesApi = mCitiesApi;
    }


    @Override
    public Observable<ResponseCity> getCities(String auth) {
        return mCitiesApi.getCities(auth)
                .compose(this.<ResponseCity>handleRetrofitRequest());
    }
}
