package it.sharengo.eteria.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.eteria.data.datasources.SharengoMapDataSource;
import it.sharengo.eteria.data.models.Address;
import rx.Observable;
import rx.functions.Action1;

@Singleton
public class AddressRepository {

    public static final String TAG = AddressRepository.class.getSimpleName();

    private SharengoMapDataSource mRemoteDataSource;

    private List<Address> mCachedAddress;

    @Inject
    public AddressRepository(SharengoMapDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }

    /**
     * Invoke API searchAddress with params received from app.
     *
     * @param  address  address to search
     * @param  format   format of address
     * @return          list address observable object
     * @see             Observable<List<Address>>
     */
    public Observable<List<Address>> searchAddress(String address, String format) {

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
    }

}
