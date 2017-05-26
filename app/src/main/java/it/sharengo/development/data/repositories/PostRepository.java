package it.sharengo.development.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.data.datasources.JsonPlaceholderDataSource;
import it.sharengo.development.data.models.Post;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton
public class PostRepository {

    public static final String TAG = PostRepository.class.getSimpleName();

    private JsonPlaceholderDataSource mRemoteDataSource;

    private Map<Integer, Post> mCachedPosts = new LinkedHashMap<>();

    @Inject
    public PostRepository(JsonPlaceholderDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }

    /*public Observable<List<Post>> getPosts() {

        List<Post> postItems = new ArrayList<>();

        return Observable.just(postItems);

    }*/


    public Observable<List<Post>> getPosts() {

        return Observable
                .concat(
                        inMemoryPosts(),
                        networkPostWithSave()
                )
                .first(new Func1<List<Post>, Boolean>() {
                    @Override
                    public Boolean call(List<Post> posts) {
                        return ! posts.isEmpty();
                    }
                });
    }

    private Observable<List<Post>> inMemoryPosts() {
        List<Post> posts = new ArrayList<>(mCachedPosts.values());
        return Observable.just(posts)
                .compose(logSource("MEMORY"));
    }

    private Observable<List<Post>> networkPostWithSave() {
        return mRemoteDataSource.getPosts()
                .doOnNext(new Action1<List<Post>>() {
                    @Override
                    public void call(List<Post> posts) {


                        for (Post post : posts) {
                            createOrUpdatePostInMemory(post);
                            //Log.w("post",": "+post.title);
                        }
                    }
                })
                .compose(logSource("NETWORK"));
    }



    // Simple logging to let us know what each source is returning
    private Observable.Transformer<List<Post>, List<Post>> logSource(final String source) {
        return new Observable.Transformer<List<Post>, List<Post>>() {
            @Override
            public Observable<List<Post>> call(Observable<List<Post>> postObservable) {
                return postObservable
                        .doOnNext(new Action1<List<Post>>() {
                            @Override
                            public void call(List<Post> postList) {
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

    private void createOrUpdatePostInMemory(Post post) {
        if (mCachedPosts == null) {
            mCachedPosts = new LinkedHashMap<>();
        }
        mCachedPosts.put(post.id, post);

        Log.w("mCachedPosts",": "+mCachedPosts);
    }
}
