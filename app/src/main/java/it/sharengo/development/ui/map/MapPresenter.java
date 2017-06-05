package it.sharengo.development.ui.map;


import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.Cars;
import it.sharengo.development.data.models.Post;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.data.repositories.CarRepository;
import it.sharengo.development.data.repositories.PostRepository;
import it.sharengo.development.ui.base.presenters.BasePresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;

public class MapPresenter extends BasePresenter<MapMvpView> {

    private static final String TAG = MapPresenter.class.getSimpleName();

    private final PostRepository mPostRepository;
    private final CarRepository mCarRepository;

    /*
     *  REQUEST
     */
    private Observable<List<Post>> mPostsRequest;
    private List<Post> mPosts;
    //----
    private Observable<Cars> mCarsRequest;
    private Observable<Cars> mPlatesRequest;
    private Observable<List<Car>> mFindPlatesRequest;
    private Cars mCars;
    private List<Car> mPlates;
    private List<SearchItem> mSearchItems;
    private boolean hideLoading;

    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();


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
        startTimer();
    }


    @Override
    protected void subscribeRequestsOnResume() {
        Log.w("AAA","subscribeRequestsOnResume");
    }


    @Override
    protected boolean showCustomLoading() {
        if(hideLoading)
            return true;
        else
            return super.showCustomLoading();
    }

    void viewCreated() {
        loadPlates();
        startTimer();
    }

    void viewDestroy() {
        stoptimertask();
    }


    public void startTimer() {

        timer = new Timer();

        timerTask = new TimerTask() {
            public void run() {

                handler.post(new Runnable() {
                    public void run() {
                        loadPlates();
                    }
                });
            }
        };

        timer.schedule(timerTask, 300000, 300000);
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Esempio
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

            }
        };
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Load Cars
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void refreshCars(float latitude, float longitude, int radius){
        hideLoading = true;
        loadCars(latitude, longitude, radius);
    }

    public void loadCars(float latitude, float longitude, int radius) {

        if( mCarsRequest == null) {
            mCarsRequest = buildCarsRequest(latitude, longitude, radius);
            addSubscription(mCarsRequest.unsafeSubscribe(getCarsSubscriber()));
        }
    }


    private Observable<Cars> buildCarsRequest(float latitude, float longitude, int radius) {
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

    private void checkResult(){
        if(mCars.reason.isEmpty()){
            getMvpView().showCars(mCars.data);
        }else{
            if(!mCars.reason.equals("No cars found"))
                getMvpView().showError(mCars.reason);

            getMvpView().noCarsFound();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Load Plates
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadPlates() {
        hideLoading = true;

        if( mPlatesRequest == null) {
            mPlatesRequest = buildPlatesRequest();
            addSubscription(mPlatesRequest.unsafeSubscribe(getPlatesSubscriber()));
        }
    }


    private Observable<Cars> buildPlatesRequest() {
        return mPlatesRequest = mCarRepository.getPlates()
                .first()
                .compose(this.<Cars>handleDataRequest());
    }

    private Subscriber<Cars> getPlatesSubscriber(){
        return new Subscriber<Cars>() {
            @Override
            public void onCompleted() {
                mPlatesRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mPlatesRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(Cars carsList) {}
        };
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Find Plates
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void findPlates(String searchText) {
        hideLoading = true;

        if( mFindPlatesRequest == null) {
            mFindPlatesRequest = buildFindPlatesRequest(searchText);
            addSubscription(mFindPlatesRequest.unsafeSubscribe(getFindPlatesSubscriber()));
        }
    }


    private Observable<List<Car>> buildFindPlatesRequest(String searchText) {
        return mFindPlatesRequest = mCarRepository.findPlates(searchText)
                .first()
                .compose(this.<List<Car>>handleDataRequest())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        checkPlatesResult();
                    }
                });
    }

    private Subscriber<List<Car>> getFindPlatesSubscriber(){
        return new Subscriber<List<Car>>() {
            @Override
            public void onCompleted() {
                mFindPlatesRequest = null;
            }

            @Override
            public void onError(Throwable e) {
                mFindPlatesRequest = null;
                //getMvpView().showError(e);
            }

            @Override
            public void onNext(List<Car> carsList) {
                mPlates = carsList;
            }
        };
    }

    private void checkPlatesResult(){

        mSearchItems = new ArrayList<SearchItem>();

        for (Car carr : mPlates){
            mSearchItems.add(new SearchItem(carr.id, "plate", carr.longitude, carr.latitude));
        }

        getMvpView().showSearchResult(mSearchItems);

    }
}



