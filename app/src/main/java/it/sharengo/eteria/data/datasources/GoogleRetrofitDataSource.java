package it.sharengo.eteria.data.datasources;

import java.util.List;

import it.sharengo.eteria.data.datasources.api.GoogleApi;
import it.sharengo.eteria.data.datasources.api.SharengoMapApi;
import it.sharengo.eteria.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.eteria.data.models.Address;
import it.sharengo.eteria.data.models.ResponseGooglePlace;
import rx.Observable;

public class GoogleRetrofitDataSource extends BaseRetrofitDataSource implements GoogleDataSource {

    private final GoogleApi mGoogleApi;


    public GoogleRetrofitDataSource(GoogleApi mGoogleApi) {
        this.mGoogleApi = mGoogleApi;
    }

    /**
     * Returns an observable object (ResponseGooglePlace) for manage API searchPlace.
     *
     * @param   query      search string (ex. an address)
     * @param   location   latitude/longitude around which to retrieve place information
     * @param   language   the language code, indicating in which language the results should be returned
     * @param   key        Google Place API KEY
     * @return           response observable object (ResponseGooglePlace)
     * @see              Observable<ResponseGooglePlace>
     */
    @Override
    public Observable<ResponseGooglePlace> searchPlace(String query, String location, String language, String key) {
        return mGoogleApi.searchPlace(query, location, language, key)
                .compose(this.<ResponseGooglePlace>handleRetrofitRequest());
    }
}
