package it.sharengo.eteria.data.datasources.base;

import android.util.Log;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import it.sharengo.eteria.data.common.ErrorResponse;
import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.functions.Func1;

public abstract class BaseRetrofitDataSource {

    protected <T> Observable.Transformer<Result<T>, T> handleRetrofitRequest() {

        return new Observable.Transformer<Result<T>, T>() {
            
            @Override
            public Observable<T> call(final Observable<Result<T>> resultObservable) {
                
                return resultObservable.flatMap(new Func1<Result<T>, Observable<T>>() {
                    
                    @Override
                    public Observable<T> call(Result<T> r) {

                        if (r.isError()) {
                            Log.e("BOMB","Error inside API ",r.error());
                            Throwable throwable = r.error();

                            if (throwable instanceof IOException) {
                                if (throwable instanceof java.net.ConnectException) {
                                    return Observable.error(new ErrorResponse(ErrorResponse.ErrorType.NO_NETWORK,throwable));
                                } else if (throwable instanceof SocketTimeoutException) {
                                    return Observable.error(new ErrorResponse(ErrorResponse.ErrorType.SERVER_TIMEOUT,throwable));
                                } else if (throwable instanceof UnknownHostException) {
                                    return Observable.error(new ErrorResponse(ErrorResponse.ErrorType.NO_NETWORK,throwable));
                                } else {
                                    return Observable.error(new ErrorResponse(ErrorResponse.ErrorType.UNEXPECTED,throwable));
                                }
                            } else {
                                return Observable.error(new ErrorResponse(ErrorResponse.ErrorType.UNEXPECTED,throwable));
                            }
                        }
                        else {
                            Response<T> retrofitResponse = r.response();
                            if (!retrofitResponse.isSuccessful()) {
                                int code = retrofitResponse.code();
                                String message = "";
                                try {
                                    message = retrofitResponse.errorBody().string();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ErrorResponse errorResponse = new ErrorResponse(ErrorResponse.ErrorType.HTTP);
                                errorResponse.httpStatus = code;
                                errorResponse.rawMessage = message;
                                return Observable.error(errorResponse);
                            }
                        }

                        return Observable.just(r.response().body());
                    }
                });
            }
        };
    }
}
