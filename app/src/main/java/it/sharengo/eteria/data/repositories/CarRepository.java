package it.sharengo.eteria.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.eteria.data.datasources.SharengoDataSource;
import it.sharengo.eteria.data.models.Car;
import it.sharengo.eteria.data.models.Response;
import it.sharengo.eteria.data.models.ResponseCar;
import it.sharengo.eteria.utils.StringsUtils;
import okhttp3.Credentials;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton
public class CarRepository {

    public static final String TAG = CarRepository.class.getSimpleName();

    private SharengoDataSource mRemoteDataSource;

    private Response mCachedCar;
    private ResponseCar mCachedReservationCar;
    private List<Car> mCachedPlate;
    private Car carSelected;

    @Inject
    public CarRepository(SharengoDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET cars
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API getCars with params received from app.
     *
     * @param  username   username of user
     * @param  password   password of user
     * @param  latitude   latitude to search
     * @param  longitude  longitude to search
     * @param  user_lat   latitude of user
     * @param  user_lon   longitude of user
     * @param  radius     radius to search
     * @return            response observable object
     * @see               Observable<Response>
     */
    public Observable<Response> getCars(String username, String password, float latitude, float longitude, float user_lat, float user_lon, int radius) {
        Log.w("getCars","XXX "+latitude);
        return mRemoteDataSource.getCars(Credentials.basic(username, StringsUtils.md5(password)), latitude, longitude, user_lat, user_lon, radius)
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET cars by plate
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API getCars with params received from app.
     *
     * @param  username   username of user
     * @param  password   password of user
     * @param  plate      plate to search
     * @return            response car observable object
     * @see               Observable<ResponseCar>
     */
    public Observable<ResponseCar> getCars(String username, String password, String plate, String callingApp) {

        return mRemoteDataSource.getCars(Credentials.basic(username, StringsUtils.md5(password)), plate, callingApp)
                .doOnNext(new Action1<ResponseCar>() {
                    @Override
                    public void call(ResponseCar response) {

                        createOrUpdateCarReservationInMemory(response);
                    }
                })
                .compose(logCarSource("NETWORK"));
    }

    private void createOrUpdateCarReservationInMemory(ResponseCar car) {
        if (mCachedReservationCar == null) {
            mCachedReservationCar = new ResponseCar();
        }
        mCachedReservationCar = car;

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              OPEN car
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API openCars with params received from app.
     *
     * @param  username   username of user
     * @param  password   password of user
     * @param  plate      plate to open
     * @param  action     action to execute
     * @return            response car observable object
     * @see               Observable<ResponseCar>
     */
    public Observable<ResponseCar> openCars(String username, String password, String plate, String action, float user_lat, float user_lon) {

        return mRemoteDataSource.openCars(Credentials.basic(username, StringsUtils.md5(password)), plate, action, user_lat, user_lon)
                .doOnNext(new Action1<ResponseCar>() {
                    @Override
                    public void call(ResponseCar response) {

                    }
                })
                .compose(logCarSource("NETWORK"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              FIND plates
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API findPlates with params received from app.
     *
     * @param  searchText  text to search
     * @return             list car observable object
     * @see                Observable<List<Car>>
     */
    public Observable<List<Car>> findPlates(final String searchText) {

        if(mCachedPlate == null){
            mCachedPlate = new ArrayList<>();
        }

        return Observable.from(mCachedPlate)
                .filter(new Func1<Car, Boolean>() {
                    @Override
                    public Boolean call(Car car) {

                        return car.id.toLowerCase().contains(searchText.toLowerCase()); //filtering
                    }
                }).toList();
    }

    /**
     * Invoke API getPlates with params received from app.
     *
     * @param  username   username of user
     * @param  password   password of user
     * @return            response observable object
     * @see               Observable<Response>
     */
    public Observable<Response> getPlates(String username, String password, float user_lat, float user_lon) {

        return mRemoteDataSource.getPlates(Credentials.basic(username, StringsUtils.md5(password)), user_lat, user_lon)
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

    private Observable.Transformer<ResponseCar, ResponseCar> logCarSource(final String source) {
        return new Observable.Transformer<ResponseCar, ResponseCar>() {
            @Override
            public Observable<ResponseCar> call(Observable<ResponseCar> postObservable) {
                return postObservable
                        .doOnNext(new Action1<ResponseCar>() {
                            @Override
                            public void call(ResponseCar postList) {
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              SET / GET car selected (popover)
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Set car selected with params received from app.
     *
     * @param  cs  car to selected
     */
    public void setCarSelected(Car cs){
        carSelected = cs;
    }

    /**
     * Return car selected.
     *
     * @return            car object
     * @see               Car
     */
    public Car getCarSelected(){
        return carSelected;
    }

}
