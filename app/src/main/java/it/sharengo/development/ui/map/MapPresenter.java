package it.sharengo.development.ui.map;


import java.util.List;

import it.sharengo.development.data.models.Post;
import it.sharengo.development.data.repositories.PostRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;

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

        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);
        mPostRepository = postRepository;
    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {
        if (mPostsRequest != null) {
            addSubscription(mPostsRequest.unsafeSubscribe(getSubscriber()));
        }
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



