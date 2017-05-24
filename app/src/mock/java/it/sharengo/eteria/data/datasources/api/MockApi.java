package it.sharengo.development.data.datasources.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.functions.Func1;

class MockApi implements SampleApi {

    public static final int REQUEST_DELAY_MILLIS = 2000;

    public static final String TAG = MockApi.class.getSimpleName();




    
    
    
    /*
     *
     *      PRIVATE UTILITIES
     * 
     */


    private <T> Observable<Result<List<T>>> buildListAsObservable(final String path, Class<T[]> clazz) {
        T[] array = new Gson().fromJson(MockUtils.loadJSONFromAsset(path), clazz);
        return MockUtils.buildSuccessResponse(Arrays.asList(array))
                .compose(this.<List<T>>handleMockRequest());
    }

    private <T> Observable<Result<T>> buildObjectAsObservableFromList(final String path, Class<T[]> clazz, Func1<T, Boolean> filterFunc) {
        T[] array = new Gson().fromJson(MockUtils.loadJSONFromAsset(path), clazz);
        return Observable.from(array)
                .filter(filterFunc)
                .first()
                .flatMap(new Func1<T, Observable<Result<T>>>() {
                    @Override
                    public Observable<Result<T>> call(T value) {
                        return MockUtils.buildSuccessResponse(value)
                                .compose(MockApi.this.<T>handleMockRequest());
                    }
                });
    }

    private <T> Observable<Result<T>> buildObjectAsObservableFromValue(T value) {
        return MockUtils.buildSuccessResponse(value)
                .compose(this.<T>handleMockRequest());
    }

    private <T> Observable<Result<T>> buildObjectAsObservable(final String path) {
        Type type = new TypeToken<T>(){}.getType();
        T object = new Gson().fromJson(MockUtils.loadJSONFromAsset(path), type);
        return MockUtils.buildSuccessResponse(object)
                .compose(this.<T>handleMockRequest());
    }

    private <T> Observable.Transformer<Result<T>, Result<T>> handleMockRequest() {
        return new Observable.Transformer<Result<T>, Result<T>>() {

            @Override
            public Observable<Result<T>> call(final Observable<Result<T>> observable) {
                return observable
                        .delay(REQUEST_DELAY_MILLIS, TimeUnit.MILLISECONDS);
            }
        };
    }
}
