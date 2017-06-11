package it.sharengo.development.data.repositories;

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
    private ResponseReservation mCachedReservation;

    @Inject
    public UserRepository(SharengoDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              User info
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public User getCachedUser(){
        return mCachedUser;
    }

    public Observable<ResponseUser> getUser() {

        return mRemoteDataSource.getUser()
                .doOnNext(new Action1<ResponseUser>() {
                    @Override
                    public void call(ResponseUser response) {

                        createOrUpdateInMemory(response);
                    }
                })
                .compose(logSource("NETWORK"));
    }

    private void createOrUpdateInMemory(ResponseUser response) {
        if (mCachedUser == null) {
            mCachedUser = new User();
        }
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

    public Observable<ResponseReservation> getReservations(boolean refreshInfo) {


        if(mCachedReservation == null || refreshInfo) {
            return mRemoteDataSource.getReservations()
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


    public Observable<ResponsePutReservation> postReservations(String plate) {

        return mRemoteDataSource.postReservations(plate)
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

    public Observable<ResponsePutReservation> deleteReservations(int id) {

        return mRemoteDataSource.deleteReservations(id)
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

    public Observable<ResponseTrip> getTrips(boolean active, boolean refreshInfo) {

        Log.w("active",": "+active);
        Log.w("mCachedTrips",": "+mCachedTrips);
        if(mCachedTrips == null || refreshInfo) {
            return mRemoteDataSource.getTrips(active)
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

}
