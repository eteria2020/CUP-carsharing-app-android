package it.sharengo.development.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.data.datasources.JsonPlaceholderDataSource;
import it.sharengo.development.data.datasources.SharengoDataSource;
import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.Cars;
import it.sharengo.development.data.models.Post;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton
public class CarRepository {

    public static final String TAG = CarRepository.class.getSimpleName();

    private SharengoDataSource mRemoteDataSource;

    private Cars mCachedCar;

    @Inject
    public CarRepository(SharengoDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }


    public Observable<Cars> getCars(float latitude, float longitude, float radius) {

        /*if(mCachedCar != null){

            return Observable.just(mCachedCar)
                    .compose(logSource("MEMORY"));

        }else {

            return mRemoteDataSource.getCars(latitude, longitude, radius)
                    .doOnNext(new Action1<Cars>() {
                        @Override
                        public void call(Cars cars) {

                            createOrUpdateCarInMemory(cars);
                        }
                    })
                    .compose(logSource("NETWORK"));
        }*/
        return mRemoteDataSource.getCars(latitude, longitude, radius)
                .doOnNext(new Action1<Cars>() {
                    @Override
                    public void call(Cars cars) {

                        createOrUpdateCarInMemory(cars);
                    }
                })
                .compose(logSource("NETWORK"));
    }



    // Simple logging to let us know what each source is returning
    private Observable.Transformer<Cars, Cars> logSource(final String source) {
        return new Observable.Transformer<Cars, Cars>() {
            @Override
            public Observable<Cars> call(Observable<Cars> postObservable) {
                return postObservable
                        .doOnNext(new Action1<Cars>() {
                            @Override
                            public void call(Cars postList) {
                                if (postList == null) {
                                    Log.d("TEST", source + " does not have any data.");
                                }
//            else if (!data.isUpToDate()) {
//                System.out.println(source + " has stale data.");
//            }
                                else {
                                    Log.d("TEST", source + " has the data you are looking for!");
                                }
                            }
                        });
            }
        };
    }

    private void createOrUpdateCarInMemory(Cars car) {
        if (mCachedCar == null) {
            mCachedCar = new Cars();
        }
        mCachedCar = car;

    }
}
