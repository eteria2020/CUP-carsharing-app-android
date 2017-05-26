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
import it.sharengo.development.data.models.Post;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton
public class CarRepository {

    public static final String TAG = CarRepository.class.getSimpleName();

    private SharengoDataSource mRemoteDataSource;

    private Map<String, Car> mCachedCar = new LinkedHashMap<>();

    @Inject
    public CarRepository(SharengoDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }


    public Observable<List<Car>> getCars(float latitude, float longitude, float radius) {

        return Observable
                .concat(
                        inMemoryPosts(),
                        networkPostWithSave(latitude, longitude, radius)
                )
                .first(new Func1<List<Car>, Boolean>() {
                    @Override
                    public Boolean call(List<Car> cars) {
                        return ! cars.isEmpty();
                    }
                });
    }

    private Observable<List<Car>> inMemoryPosts() {
        List<Car> cars = new ArrayList<>(mCachedCar.values());
        return Observable.just(cars)
                .compose(logSource("MEMORY"));
    }

    private Observable<List<Car>> networkPostWithSave(float latitude, float longitude, float radius) {
        return mRemoteDataSource.getCars(latitude, longitude, radius)
                .doOnNext(new Action1<List<Car>>() {
                    @Override
                    public void call(List<Car> cars) {


                        for (Car car : cars) {
                            createOrUpdateCarInMemory(car);
                            //Log.w("post",": "+post.title);
                        }
                    }
                })
                .compose(logSource("NETWORK"));
    }



    // Simple logging to let us know what each source is returning
    private Observable.Transformer<List<Car>, List<Car>> logSource(final String source) {
        return new Observable.Transformer<List<Car>, List<Car>>() {
            @Override
            public Observable<List<Car>> call(Observable<List<Car>> postObservable) {
                return postObservable
                        .doOnNext(new Action1<List<Car>>() {
                            @Override
                            public void call(List<Car> postList) {
                                if (postList == null || postList.isEmpty()) {
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

    private void createOrUpdateCarInMemory(Car car) {
        if (mCachedCar == null) {
            mCachedCar = new LinkedHashMap<>();
        }
        mCachedCar.put(car.id, car);

        Log.w("mCachedCar",": "+mCachedCar);
    }
}
