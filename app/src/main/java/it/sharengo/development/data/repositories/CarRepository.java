package it.sharengo.development.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.data.datasources.SharengoDataSource;
import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton
public class CarRepository {

    public static final String TAG = CarRepository.class.getSimpleName();

    private SharengoDataSource mRemoteDataSource;

    private Response mCachedCar;
    private List<Car> mCachedPlate;

    @Inject
    public CarRepository(SharengoDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }


    public Observable<Response> getCars(float latitude, float longitude, int radius) {

        return mRemoteDataSource.getCars(latitude, longitude, radius)
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {

                        createOrUpdateCarInMemory(response);
                    }
                })
                .compose(logSource("NETWORK"));
    }

    private void createOrUpdateCarInMemory(Response car) {
        if (mCachedCar == null) {
            mCachedCar = new Response();
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

    public Observable<Response> getPlates() {

        return mRemoteDataSource.getPlates()
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {

                        createOrUpdatePlateInMemory(response);
                    }
                })
                .compose(logSource("NETWORK"));

    }

    private void createOrUpdatePlateInMemory(Response response) {
        if (mCachedPlate == null) {
            mCachedPlate = new ArrayList<Car>();
        }
        mCachedPlate = response.data;
    }

    private Observable.Transformer<Response, Response> logSource(final String source) {
        return new Observable.Transformer<Response, Response>() {
            @Override
            public Observable<Response> call(Observable<Response> postObservable) {
                return postObservable
                        .doOnNext(new Action1<Response>() {
                            @Override
                            public void call(Response postList) {
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
