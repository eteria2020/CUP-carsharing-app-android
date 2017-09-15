package it.sharengo.eteria.data.datasources;

import java.util.List;

import it.sharengo.eteria.data.datasources.api.GoogleApi;
import it.sharengo.eteria.data.datasources.api.SharengoMapApi;
import it.sharengo.eteria.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.eteria.data.models.Address;
import it.sharengo.eteria.data.models.ResponseGooglePlace;
import it.sharengo.eteria.data.models.ResponseGoogleRoutes;
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

    /**
     * Returns an observable object (ResponseGoogleRoutes) for manage API searchPlace.
     *
     * @param   origin        latitude/longitude for the first point. Formato: lat,lon
     * @param   destination   latitude/longitude for the second point. Formato: lat,lon
     * @param   mode        the mode of transport to use when calculating directions (see: https://developers.google.com/maps/documentation/directions/intro#TravelModes)
     * @param   key        Google API KEY
     * @return           response observable object (ResponseGoogleRoutes)
     * @see              Observable<ResponseGoogleRoutes>
     */
    @Override
    public Observable<ResponseGoogleRoutes> getRoutes(String origin, String destination, String mode, String key) {
        return mGoogleApi.getRoutes(origin, destination, mode, key)
                .compose(this.<ResponseGoogleRoutes>handleRetrofitRequest());
    }
}
