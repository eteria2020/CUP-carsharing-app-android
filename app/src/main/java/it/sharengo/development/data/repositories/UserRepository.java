package it.sharengo.development.data.repositories;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.data.datasources.SharengoDataSource;
import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.User;
import rx.Observable;
import rx.functions.Action1;

@Singleton
public class UserRepository {

    public static final String TAG = UserRepository.class.getSimpleName();

    private SharengoDataSource mRemoteDataSource;

    private User mCachedUser;

    @Inject
    public UserRepository(SharengoDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }

    public User getCachedUser(){
        return mCachedUser;
    }

    public Observable<Response> getUser() {

        return mRemoteDataSource.getUser()
                .doOnNext(new Action1<Response>() {
                    @Override
                    public void call(Response response) {

                        createOrUpdateInMemory(response);
                    }
                })
                .compose(logSource("NETWORK"));
    }

    private void createOrUpdateInMemory(Response response) {
        if (mCachedUser == null) {
            mCachedUser = new User();
        }
        mCachedUser = response.user;

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
