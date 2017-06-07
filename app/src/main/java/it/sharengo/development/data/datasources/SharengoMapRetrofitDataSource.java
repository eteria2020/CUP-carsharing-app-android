package it.sharengo.development.data.datasources;

import java.util.List;

import it.sharengo.development.data.datasources.api.SharengoApi;
import it.sharengo.development.data.datasources.api.SharengoMapApi;
import it.sharengo.development.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.development.data.models.Address;
import it.sharengo.development.data.models.Cars;
import rx.Observable;

import static android.R.attr.radius;

public class SharengoMapRetrofitDataSource extends BaseRetrofitDataSource implements SharengoMapDataSource {

    private final SharengoMapApi mSharengoMapApi;


    public SharengoMapRetrofitDataSource(SharengoMapApi mSharengoMapApi) {
        this.mSharengoMapApi = mSharengoMapApi;
    }

    @Override
    public Observable<List<Address>> searchAddress(String address, String format) {
        return mSharengoMapApi.searchAddress(address, format)
                .compose(this.<List<Address>>handleRetrofitRequest());
    }
}
