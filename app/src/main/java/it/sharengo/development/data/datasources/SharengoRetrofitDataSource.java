package it.sharengo.development.data.datasources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.sharengo.development.data.datasources.api.JsonPlaceholderApi;
import it.sharengo.development.data.datasources.api.SharengoApi;
import it.sharengo.development.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.Cars;
import it.sharengo.development.data.models.Post;
import rx.Observable;

public class SharengoRetrofitDataSource extends BaseRetrofitDataSource implements SharengoDataSource {

    private final SharengoApi mSharengoApi;


    public SharengoRetrofitDataSource(SharengoApi mSharengoApi) {
        this.mSharengoApi = mSharengoApi;
    }


    @Override
    public Observable<Cars> getCars(float latitude, float longitude, int radius) {
        return mSharengoApi.getCars(latitude, longitude, radius)
                .compose(this.<Cars>handleRetrofitRequest());
    }
}
