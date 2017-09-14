package it.sharengo.eteria.data.datasources;

import java.util.List;

import it.sharengo.eteria.data.datasources.api.SharengoMapApi;
import it.sharengo.eteria.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.eteria.data.models.Address;
import rx.Observable;

public class SharengoMapRetrofitDataSource extends BaseRetrofitDataSource implements SharengoMapDataSource {

    private final SharengoMapApi mSharengoMapApi;


    public SharengoMapRetrofitDataSource(SharengoMapApi mSharengoMapApi) {
        this.mSharengoMapApi = mSharengoMapApi;
    }

    /**
     * Returns an observable object (List<Address>) for manage API searchAddress.
     *
     * @param   address  identification credentials
     * @param   format   latitude to search
     * @return           list address observable object
     * @see              Observable<List<Address>>
     */
    @Override
    public Observable<List<Address>> searchAddress(String address, String format) {
        return mSharengoMapApi.searchAddress(address, format)
                .compose(this.<List<Address>>handleRetrofitRequest());
    }
}
