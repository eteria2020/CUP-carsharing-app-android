package it.sharengo.development.data.repositories;

import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.data.datasources.SharengoDataSource;
import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.Reservation;
import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponsePutReservation;
import it.sharengo.development.data.models.ResponseReservation;
import it.sharengo.development.data.models.ResponseTrip;
import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.data.models.User;
import it.sharengo.development.utils.StringsUtils;
import okhttp3.Credentials;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton
public class UserRepository {

    public static final String TAG = UserRepository.class.getSimpleName();

    private SharengoDataSource mRemoteDataSource;

    private User mCachedUser;
    private List<Reservation> mCachedReservations;
    private ResponseTrip mCachedTrips;
    private ResponseTrip mCachedTripsChron;
    private ResponseReservation mCachedReservation;

    @Inject
    public UserRepository(SharengoDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              User credentials
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Save user credentials or update if also exist.
     *
     * @param  username  username of user
     * @param  password  password of user
     */
    public void saveUserCredentials(String username, String password){
        if(mCachedUser == null)
            mCachedUser = new User(username, password, "");
        else {
            mCachedUser.username = username;
            mCachedUser.password = password;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Logout
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Delete from preference login data of user.
     *
     * @param  prefs  shared preference of app
     */
    public void logoutUser(SharedPreferences prefs){
        mCachedUser = new User("", "", "");

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PreferencesRepository.KEY_USERNAME, "");
        editor.putString(PreferencesRepository.KEY_PASSWORD, "");
        editor.commit();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              User info
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return from cache user's object.
     *
     * @return      user's object
     * @see         User
     */
    public User getCachedUser(){
        return mCachedUser;
    }

    /**
     * Returns user information after invoke method to comunicate with server for API getUser.
     *
     * @param  username  username of user
     * @param  password  password of user
     * @return           response user of observable object
     * @see              Observable<ResponseUser>
     */
    public Observable<ResponseUser> getUser(String username, String password) {

        return mRemoteDataSource.getUser(Credentials.basic(username, StringsUtils.md5(password)))
                .doOnNext(new Action1<ResponseUser>() {
                    @Override
                    public void call(ResponseUser response) {

                        createOrUpdateInMemory(response);
                    }
                })
                .compose(logSource("NETWORK"));
    }

    private void createOrUpdateInMemory(ResponseUser response) {
        Log.w("mCachedUser",": "+mCachedUser);
        if (mCachedUser == null) {
            mCachedUser = new User();
        }
        Log.w("response.user",": "+response.user);
        mCachedUser.userInfo = response.user;
    }

    private Observable.Transformer<ResponseUser, ResponseUser> logSource(final String source) {
        return new Observable.Transformer<ResponseUser, ResponseUser>() {
            @Override
            public Observable<ResponseUser> call(Observable<ResponseUser> postObservable) {
                return postObservable
                        .doOnNext(new Action1<ResponseUser>() {
                            @Override
                            public void call(ResponseUser postList) {
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
    //                                              GET Reservation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve reservation from server if refreshInfo if true else retrieve from cache.
     *
     * @param  username     username of user
     * @param  password     password of user
     * @param  refreshInfo  refresh or not the information
     * @return              response reservation observable object
     * @see                 Observable<ResponseReservation>
     */
    public Observable<ResponseReservation> getReservations(String username, String password, boolean refreshInfo) {


        if(mCachedReservation == null || refreshInfo) {
            return mRemoteDataSource.getReservations(Credentials.basic(username, StringsUtils.md5(password)))
                    .doOnNext(new Action1<ResponseReservation>() {
                        @Override
                        public void call(ResponseReservation response) {

                            createOrUpdateInMemory(response);
                        }
                    })
                    .compose(logSourceReservation("NETWORK"));
        }else{
            return Observable.just(mCachedReservation);
        }
    }

    private void createOrUpdateInMemory(ResponseReservation response) {

        if (mCachedReservation == null) {
            mCachedReservation = new ResponseReservation();
        }
        mCachedReservation = response;
    }

    private Observable.Transformer<ResponseReservation, ResponseReservation> logSourceReservation(final String source) {
        return new Observable.Transformer<ResponseReservation, ResponseReservation>() {
            @Override
            public Observable<ResponseReservation> call(Observable<ResponseReservation> postObservable) {
                return postObservable
                        .doOnNext(new Action1<ResponseReservation>() {
                            @Override
                            public void call(ResponseReservation postList) {
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
    //                                              PUT Reservation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API postReservations with params received from app.
     *
     * @param  username  username of user
     * @param  password  password of user
     * @param  plate     plate
     * @param  user_lat  latitude of user
     * @param  user_lon  longitude of user
     * @return           response put reservation observable object
     * @see              Observable<ResponsePutReservation>
     */
    public Observable<ResponsePutReservation> postReservations(String username, String password, String plate, float user_lat, float user_lon) {

        return mRemoteDataSource.postReservations(Credentials.basic(username, StringsUtils.md5(password)), plate, user_lat, user_lon)
                .doOnNext(new Action1<ResponsePutReservation>() {
                    @Override
                    public void call(ResponsePutReservation response) {

                        createOrUpdateReservationInMemory(response);
                    }
                })
                .compose(logSourcePutReservation("NETWORK"));
    }

    private void createOrUpdateReservationInMemory(ResponsePutReservation response) {
        /*if (mCachedReservations == null) {
            mCachedReservations = new ArrayList<Reservation>();
        }
        mCachedReservations = response.reservations;*/

        Log.w("RES",": "+response);
    }

    private Observable.Transformer<ResponsePutReservation, ResponsePutReservation> logSourcePutReservation(final String source) {
        return new Observable.Transformer<ResponsePutReservation, ResponsePutReservation>() {
            @Override
            public Observable<ResponsePutReservation> call(Observable<ResponsePutReservation> postObservable) {
                return postObservable
                        .doOnNext(new Action1<ResponsePutReservation>() {
                            @Override
                            public void call(ResponsePutReservation postList) {
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
    //                                              DELETE Reservation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API deleteReservations with params received from app.
     *
     * @param  username  username of user
     * @param  password  password of user
     * @return           response put reservation observable object
     * @see              Observable<ResponsePutReservation>
     */
    public Observable<ResponsePutReservation> deleteReservations(String username, String password, int id) {

        return mRemoteDataSource.deleteReservations(Credentials.basic(username, StringsUtils.md5(password)), id)
                .doOnNext(new Action1<ResponsePutReservation>() {
                    @Override
                    public void call(ResponsePutReservation response) {

                    }
                });
                //.compose(logSourceDeleteReservation("NETWORK"));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Trips
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API getTrips with params received from app. Retrieve from cache if
     * refreshInfo it's false.
     *
     * @param  username     username of user
     * @param  password     password of user
     * @param  active       status of trip
     * @param  refreshInfo  boolean for retrieve data from server or cache
     * @return              response trip observable object
     * @see                 Observable<ResponseTrip>
     */
    public Observable<ResponseTrip> getTrips(String username, String password, boolean active, boolean refreshInfo) {

        if(mCachedTrips == null || refreshInfo) {
            return mRemoteDataSource.getTrips(Credentials.basic(username, StringsUtils.md5(password)), active)
                    .doOnNext(new Action1<ResponseTrip>() {
                        @Override
                        public void call(ResponseTrip response) {

                            createOrUpdateTripsInMemory(response);
                        }
                    })
                    .compose(logSourceTrips("NETWORK"));
        }else{
            return Observable.just(mCachedTrips);
        }
    }

    private void createOrUpdateTripsInMemory(ResponseTrip response) {
        if (mCachedTrips == null) {
            mCachedTrips = new ResponseTrip();
        }
        mCachedTrips = response;
    }

    private Observable.Transformer<ResponseTrip, ResponseTrip> logSourceTrips(final String source) {
        return new Observable.Transformer<ResponseTrip, ResponseTrip>() {
            @Override
            public Observable<ResponseTrip> call(Observable<ResponseTrip> postObservable) {
                return postObservable
                        .doOnNext(new Action1<ResponseTrip>() {
                            @Override
                            public void call(ResponseTrip postList) {
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
    //                                              Trips (old)
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Invoke API getTrips with params received from app by chron setting.
     *
     * @param  username     username of user
     * @param  password     password of user
     * @return              response trip observable object
     * @see                 Observable<ResponseTrip>
     */
    public Observable<ResponseTrip> getChronTrips(String username, String password) {

        return mRemoteDataSource.getTrips(Credentials.basic(username, StringsUtils.md5(password)), false)
                .doOnNext(new Action1<ResponseTrip>() {
                    @Override
                    public void call(ResponseTrip response) {

                        createOrUpdateTripChronsInMemory(response);
                    }
                })
                .compose(logSourceTrips("NETWORK"));
    }

    private void createOrUpdateTripChronsInMemory(ResponseTrip response) {
        if (mCachedTripsChron == null) {
            mCachedTripsChron = new ResponseTrip();
        }
        mCachedTripsChron = response;
    }

    private Observable.Transformer<ResponseTrip, ResponseTrip> logSourceTripsChron(final String source) {
        return new Observable.Transformer<ResponseTrip, ResponseTrip>() {
            @Override
            public Observable<ResponseTrip> call(Observable<ResponseTrip> postObservable) {
                return postObservable
                        .doOnNext(new Action1<ResponseTrip>() {
                            @Override
                            public void call(ResponseTrip postList) {
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
