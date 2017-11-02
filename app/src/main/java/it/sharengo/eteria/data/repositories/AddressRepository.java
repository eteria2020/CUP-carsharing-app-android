package it.sharengo.eteria.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.eteria.data.datasources.GoogleDataSource;
import it.sharengo.eteria.data.datasources.SharengoMapDataSource;
import it.sharengo.eteria.data.models.Address;
import it.sharengo.eteria.data.models.GooglePlace;
import it.sharengo.eteria.data.models.ResponseGooglePlace;
import it.sharengo.eteria.data.models.ResponseGoogleRoutes;
import rx.Observable;
import rx.functions.Action1;

@Singleton
public class AddressRepository {

    public static final String TAG = AddressRepository.class.getSimpleName();

    private SharengoMapDataSource mRemoteDataSource;
    private GoogleDataSource mGoogleRemoteDataSource;

    private List<Address> mCachedAddress;
    private List<GooglePlace> mCachedPlace;
    private ResponseGoogleRoutes mGoogleRoutes;

    @Inject
    public AddressRepository(SharengoMapDataSource remoteDataSource, GoogleDataSource googleRemoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
        this.mGoogleRemoteDataSource = googleRemoteDataSource;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              FIND address
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API searchAddress with params received from app.
     *
     * @param  address  address to search
     * @param  format   format of address
     * @return          list address observable object
     * @see             Observable<List<Address>>
     */
    /*public Observable<List<Address>> searchAddress(String address, String format) {

        return mRemoteDataSource.searchAddress(address, format)
                .doOnNext(new Action1<List<Address>>() {
                    @Override
                    public void call(List<Address> address) {

                        createOrUpdateAddressInMemory(address);
                    }
                })
                .compose(logSource("NETWORK"));
    }


    private void createOrUpdateAddressInMemory(List<Address> address) {
        if (mCachedAddress == null) {
            mCachedAddress = new ArrayList<Address>();
        }
        mCachedAddress = address;

    }




    private Observable.Transformer<List<Address>, List<Address>> logSource(final String source) {
        return new Observable.Transformer<List<Address>, List<Address>>() {
            @Override
            public Observable<List<Address>> call(Observable<List<Address>> postObservable) {
                return postObservable
                        .doOnNext(new Action1<List<Address>>() {
                            @Override
                            public void call(List<Address> addressList) {
                                if (addressList == null) {
                                    Log.d("TEST", source + " does not have any data.");
                                }
                                else {
                                    Log.d("TEST", source + " has the data you are looking for!");
                                }
                            }
                        });
            }
        };
    }*/

    /**
     * Invoke API searchPlace with params received from app (Google Place service)
     *
     * @param   query      search string (ex. an address)
     * @param   location   latitude/longitude around which to retrieve place information
     * @param   language   the language code, indicating in which language the results should be returned
     * @param   key        Google Place API KEY
     * @return           response observable object (ResponseGooglePlace)
     * @see             Observable<List<Address>>
     */
    public Observable<ResponseGooglePlace> searchPlace(String query, String location, String language, String key) {

        return mGoogleRemoteDataSource.searchPlace(query, location, language, key)
                .doOnNext(new Action1<ResponseGooglePlace>() {
                    @Override
                    public void call(ResponseGooglePlace response) {

                        createOrUpdatePlacesInMemory(response);
                    }
                })
                .compose(logSourcePlace("NETWORK"));
    }


    private void createOrUpdatePlacesInMemory(ResponseGooglePlace response) {
        if (mCachedPlace == null) {
            mCachedPlace = new ArrayList<GooglePlace>();
        }
        mCachedPlace = response.results;
    }


    private Observable.Transformer<ResponseGooglePlace, ResponseGooglePlace> logSourcePlace(final String source) {
        return new Observable.Transformer<ResponseGooglePlace, ResponseGooglePlace>() {
            @Override
            public Observable<ResponseGooglePlace> call(Observable<ResponseGooglePlace> postObservable) {
                return postObservable
                        .doOnNext(new Action1<ResponseGooglePlace>() {
                            @Override
                            public void call(ResponseGooglePlace placeResult) {
                                if (placeResult == null) {
                                    Log.d("TEST", source + " does not have any data.");
                                }
                                else {
                                    Log.d("TEST", source + " has the data you are looking for!");
                                }
                            }
                        });
            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET routes
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Invoke API getRoutes with params received from app (Google Place service)
     *
     * @param   origin        latitude/longitude for the first point. Formato: lat,lon
     * @param   destination   latitude/longitude for the second point. Formato: lat,lon
     * @param   mode        the mode of transport to use when calculating directions (see: https://developers.google.com/maps/documentation/directions/intro#TravelModes)
     * @param   key        Google API KEY
     * @return           response observable object (ResponseGoogleRoutes)
     * @see              Observable<ResponseGoogleRoutes>
     */
    public Observable<ResponseGoogleRoutes> getRoutes(String origin, String destination, String mode, String key) {

        return mGoogleRemoteDataSource.getRoutes(origin, destination, mode, key)
                .doOnNext(new Action1<ResponseGoogleRoutes>() {
                    @Override
                    public void call(ResponseGoogleRoutes response) {

                        createOrUpdateRoutesInMemory(response);
                    }
                })
                .compose(logSourceRoutes("NETWORK"));
    }


    private void createOrUpdateRoutesInMemory(ResponseGoogleRoutes response) {
        if (mGoogleRoutes == null) {
            mGoogleRoutes = new ResponseGoogleRoutes();
        }
        mGoogleRoutes = response;
    }


    private Observable.Transformer<ResponseGoogleRoutes, ResponseGoogleRoutes> logSourceRoutes(final String source) {
        return new Observable.Transformer<ResponseGoogleRoutes, ResponseGoogleRoutes>() {
            @Override
            public Observable<ResponseGoogleRoutes> call(Observable<ResponseGoogleRoutes> postObservable) {
                return postObservable
                        .doOnNext(new Action1<ResponseGoogleRoutes>() {
                            @Override
                            public void call(ResponseGoogleRoutes placeResult) {
                                if (placeResult == null) {
                                    Log.d("TEST", source + " does not have any data.");
                                }
                                else {
                                    Log.d("TEST", source + " has the data you are looking for!");
                                }
                            }
                        });
            }
        };
    }
}
