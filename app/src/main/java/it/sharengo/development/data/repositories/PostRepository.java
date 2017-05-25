package it.sharengo.development.data.repositories;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.data.datasources.JsonPlaceholderDataSource;
import it.sharengo.development.data.models.Post;
import it.sharengo.development.injection.RemoteDataSource;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

/**
 * Created by gretaiaconisi on 25/05/17.
 */

@Singleton
public class PostRepository {
    public static final String TAG = AppRepository.class.getSimpleName();

    private final JsonPlaceholderDataSource mRemoteDataSource;
    private Map<Integer, Post> mCachedPosts;

    private final BehaviorSubject<List<Post>> mPostSubject = BehaviorSubject.create();

    @Inject
    public PostRepository(@RemoteDataSource JsonPlaceholderDataSource remoteDataSource) {
        mRemoteDataSource = remoteDataSource;
    }



    public Observable<List<Post>> getPosts() {

        if(mCachedPosts != null) {
            return mPostSubject;
        }
        else {
            mCachedPosts = new LinkedHashMap<>();
        }

        return mRemoteDataSource.getPosts()
                .doOnNext(new Action1<List<Post>>() {
                    @Override
                    public void call(List<Post> posts) {
                        mPostSubject.onNext(posts);
                        for (Post post : posts) {
                            mCachedPosts.put(post.id, post);
                        }
                    }
                });

    }
}
