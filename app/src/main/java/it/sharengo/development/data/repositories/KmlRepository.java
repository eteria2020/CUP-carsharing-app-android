package it.sharengo.development.data.repositories;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.data.datasources.KmlDataSource;
import it.sharengo.development.data.models.Kml;
import rx.Observable;
import rx.functions.Action1;

@Singleton
public class KmlRepository {

    public static final String TAG = KmlRepository.class.getSimpleName();

    private KmlDataSource mRemoteDataSource;

    private Kml mCachedKml;

    @Inject
    public KmlRepository(KmlDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }

    /**
     * Invoke API from retrieve Kml zone to draw on map.
     *
     * @param  context  context of application
     * @return          observable kml object
     * @see             Observable<Kml>
     */
    public Observable<Kml> zone(Context context) {

        return mRemoteDataSource.zone()
                .doOnNext(new Action1<Kml>() {
                    @Override
                    public void call(Kml kml) {

                        createOrUpdateKmlInMemory(kml);
                    }
                })
                .compose(logSource("NETWORK"));
    }


    private void createOrUpdateKmlInMemory(Kml kml) {
        /*if (mCachedKml == null) {
            mCachedKml = new ArrayKml();
        }*/
        mCachedKml = kml;

    }




    private Observable.Transformer<Kml, Kml> logSource(final String source) {
        return new Observable.Transformer<Kml, Kml>() {
            @Override
            public Observable<Kml> call(Observable<Kml> postObservable) {
                return postObservable
                        .doOnNext(new Action1<Kml>() {
                            @Override
                            public void call(Kml kmlList) {
                                if (kmlList == null) {
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
