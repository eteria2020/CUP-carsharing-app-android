package it.sharengo.development.ui.map;


import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.models.Post;
import it.sharengo.development.data.repositories.PostRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.functions.Func2;

public class MapPresenter extends BasePresenter<MapMvpView> {

    private static final String TAG = MapPresenter.class.getSimpleName();

    private final PostRepository mPostRepository;

    /*
     *  REQUEST
     */
    private Observable<List<Post>> mPostsRequest;
    private List<Post> mPosts;


    public MapPresenter(SchedulerProvider schedulerProvider,
                        PostRepository postRepository) {
        super(schedulerProvider);
        mPostRepository = postRepository;

        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);

    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    void viewCreated() {
        loadPosts();
    }

    private void loadPosts() {
        if(mPosts == null && mPostsRequest == null) {
            mPostsRequest = buildPostsRequest();
            addSubscription(mPostsRequest.unsafeSubscribe(getSubscriber()));
        }
    }

    private Observable<List<Post>> buildPostsRequest() {
        return mPostsRequest = mPostRepository.getPosts()
                .first()
                .compose(this.<List<Post>>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        //getMvpView().showUsers(mUsers);
                    }
                });

        //addSubscription(mPostsRequest.subscribe(getSubscriber()));
    }

    private Subscriber<List<Post>> getSubscriber(){
        return new Subscriber<List<Post>>() {
            @Override
            public void onCompleted() {
                mPostsRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mPostsRequest = null;
                getMvpView().showError(e);
            }

            @Override
            public void onNext(List<Post> postList) {
                mPosts = postList;
            }
        };
    }
}



