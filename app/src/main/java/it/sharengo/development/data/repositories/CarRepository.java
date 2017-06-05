package it.sharengo.development.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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
    private List<Car> mCachedPlate;

    @Inject
    public CarRepository(SharengoDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }


    public Observable<Cars> getCars(float latitude, float longitude, int radius) {

        return mRemoteDataSource.getCars(latitude, longitude, radius)
                .doOnNext(new Action1<Cars>() {
                    @Override
                    public void call(Cars cars) {

                        createOrUpdateCarInMemory(cars);
                    }
                })
                .compose(logSource("NETWORK"));
    }

    private void createOrUpdateCarInMemory(Cars car) {
        if (mCachedCar == null) {
            mCachedCar = new Cars();
        }
        mCachedCar = car;

    }


    public Observable<List<Car>> findPlates(final String searchText) {
            return Observable.from(mCachedPlate)
                    .filter(new Func1<Car, Boolean>() {
                        @Override
                        public Boolean call(Car car) {
                            return car.id.toLowerCase().contains(searchText.toLowerCase()); //filtering
                        }
                    }).toList();
    }

    public Observable<Cars> getPlates() {

        return mRemoteDataSource.getPlates()
                .doOnNext(new Action1<Cars>() {
                    @Override
                    public void call(Cars cars) {

                        createOrUpdatePlateInMemory(cars);
                    }
                })
                .compose(logSource("NETWORK"));

    }

    private void createOrUpdatePlateInMemory(Cars cars) {
        if (mCachedPlate == null) {
            mCachedPlate = new ArrayList<Car>();
        }

        mCachedPlate = cars.data;
    }

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
                                else {
                                    Log.d("TEST", source + " has the data you are looking for!");
                                }
                            }
                        });
            }
        };
    }

}
