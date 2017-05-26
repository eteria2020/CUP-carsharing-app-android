package it.sharengo.development.ui.map;


import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.Cars;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.models.Post;
import it.sharengo.development.data.repositories.CarRepository;
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
    private final CarRepository mCarRepository;

    /*
     *  REQUEST
     */
    private Observable<List<Post>> mPostsRequest;
    private List<Post> mPosts;
    private Observable<Cars> mCarsRequest;
    private Cars mCars;


    public MapPresenter(SchedulerProvider schedulerProvider,
                        PostRepository postRepository,
                        CarRepository carRepository) {
        super(schedulerProvider);
        mPostRepository = postRepository;
        mCarRepository = carRepository;

        //mAppRepository.selectMenuItem(MenuItem.Section.HOME);

    }


    @Override
    protected void restoreDataOnConfigurationChange() {

    }


    @Override
    protected void subscribeRequestsOnResume() {

    }

    void viewCreated() {
        //loadPosts();
        loadCars((float) 45.1456, (float) 12.4543, (float) 100.00);
    }

    private void loadPosts() {
        if(mPosts == null && mPostsRequest == null) {
            mPostsRequest = buildPostsRequest();
            addSubscription(mPostsRequest.unsafeSubscribe(getSubscriber()));
        }
    }

    private void loadCars(float latitude, float longitude, float radius) {
        if(mCars == null && mCarsRequest == null) {
            mCarsRequest = buildCarsRequest(latitude, longitude, radius);
            addSubscription(mCarsRequest.unsafeSubscribe(getCarsSubscriber()));
        }
    }

    private Observable<List<Post>> buildPostsRequest() {
        return mPostsRequest = mPostRepository.getPosts()
                .first()
                .compose(this.<List<Post>>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {

                    }
                });
    }

    private void checkResult(){
        if(mCars.reason.isEmpty()){
            getMvpView().showCars(mCars.data);
        }else{
            getMvpView().showError(mCars.reason);
        }
    }

    private Observable<Cars> buildCarsRequest(float latitude, float longitude, float radius) {
        return mCarsRequest = mCarRepository.getCars(latitude, longitude, radius)
                .first()
                .compose(this.<Cars>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkResult();
                    }
                });
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

                //Log.w("getSubscriber",": "+mPosts);
            }
        };
    }

    private Subscriber<Cars> getCarsSubscriber(){
        return new Subscriber<Cars>() {
            @Override
            public void onCompleted() {
                mCarsRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mCarsRequest = null;
                getMvpView().showError(e);
            }

            @Override
            public void onNext(Cars carsList) {
                mCars = carsList;
            }
        };
    }
}



