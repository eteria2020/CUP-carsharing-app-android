package it.sharengo.development.data.datasources.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import it.sharengo.development.App;
import it.sharengo.development.data.common.ErrorResponse;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

class MockUtils {

    public static final String TAG = MockUtils.class.getSimpleName();

    public static <T> Observable<Result<T>> buildSuccessResponse(final T o) {
        Response<T> response = Response.success(o);
        final Result<T> result = Result.response(response);
        return Observable.just(result)
                .compose(MockUtils.<T>handleMockRequest());
    }

    public static Observable<Result<Object>> buildSuccessResponse() {
        Response<Object> response = Response.success(new Object());
        final Result<Object> result = Result.response(response);
        return Observable.just(result)
                .compose(MockUtils.<Object>handleMockRequest());
    }

    public static <T> Observable<Result<T>> buildSuccessResponse(String jsonPath, Class modelClass) {
        final String jsonString = MockUtils.loadJSONFromAsset(jsonPath);
        T mockModel = (T) new Gson().fromJson(jsonString, modelClass);

        Response<T> response = Response.success(mockModel);
        final Result<T> result = Result.response(response);
        return Observable.just(result)
                .compose(MockUtils.<T>handleMockRequest());
    }

    public static <T> Observable<Result<T>> buildErrorResponse(int statusCode) {
        Response<T>response = Response.error(
                statusCode,
                getErrorResponseBody(getErrorMessageByCode(statusCode))
        );
        final Result<T> result = Result.response(response);
        return Observable.just(result)
                .compose(MockUtils.<T>handleMockRequest());
    }

    public static ResponseBody getErrorResponseBody(String body) {
        return ResponseBody.create(okhttp3.MediaType.parse("text"), body);
    }

    public static String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = App.getInstance().getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            Log.e("MockUtils", e.getMessage());
            return "";
        }
        return json;
    }

    public static String getErrorMessageByCode(int statusCode) {
        String errorMessage = "Unknown error";
        switch(statusCode) {
            /*
             * La richiesta non può essere soddisfatta a causa di errori di sintassi.
             */
            case 400:
                errorMessage = "Bad Request";
                break;
            /*
             * Simile a 403/Forbidden, ma pensato per essere usato quando l'autenticazione è
             * possibile ma è fallita o non può essere fornita. Vedi anche basic access
             * authentication e digest access authentication.
             */
            case 401:
                errorMessage = "Unauthorized";
                break;
            /*
             * La richiesta è legittima ma il server si rifiuta di soddisfarla. Contrariamente al
             * codice 401 Unauthorized, l'autenticazione non ha effetto.
             */
            case 403:
                errorMessage = "Forbidden";
                break;
            /*
             * La risorsa richiesta non è stata trovata ma in futuro potrebbe essere disponibile.
             */
            case 404:
                errorMessage = "Not Found";
                break;
            /*
             * La richiesta è stata eseguita usando un metodo non permesso. Ad esempio questo accade quando si usa il metodo GET per inviare dati da presentare con un metodo POST.
             */
            case 405:
                errorMessage = "Method Not Allowed";
                break;
            /*
             * La risorsa richiesta è solo in grado di generare contenuti non accettabili secondo la header Accept inviato nella richiesta.[2]
             */
            case 406:
                errorMessage = "Not Acceptable";
                break;
            /*
             * Il tempo per inviare la richiesta è scaduto e il server ha terminato la connessione.
             */
            case 408:
                errorMessage = "Request Timeout";
                break;
            /*
             * La richiesta non può essere portata a termine a causa di un conflitto con lo stato attuale della risorsa.
             */
            case 409:
                errorMessage = "Conflict";
        }

        return errorMessage;
    }



    private static <T> Observable.Transformer<Result<T>, Result<T>> handleMockRequest() {
        return new Observable.Transformer<Result<T>, Result<T>>() {
            @Override
            public Observable<Result<T>> call(Observable<Result<T>> observable) {
                final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

                ConnectivityManager cm = (ConnectivityManager) App.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if(!isConnected) {
                    return Observable.error(new ErrorResponse(ErrorResponse.ErrorType.NO_NETWORK));
                }
                
                return observable
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                printRequestStart(stackTraceElements);
                            }
                        })
                        .doOnNext(new Action1<Result<T>>() {
                            @Override
                            public void call(Result<T> t) {
                                printRequestContent(stackTraceElements, t);
                            }
                        })
                        .doOnTerminate(new Action0() {
                            @Override
                            public void call() {
//                                printRequestEnd(stackTraceElements);
                            }
                        });
            }
        };
    }

    private static void printRequestStart(StackTraceElement[] elements) {
        Pair<String, String> pair = getCallerAndRequest(elements);
        try {
            Log.d("Retrofit(mock)", "--> " + pair.second + " launched from " + pair.first);
        } catch(Exception ignored) {}
    }

    private static void printRequestContent(StackTraceElement[] elements, Result result) {
        try {
            Log.d("Retrofit(mock)", "<-- Response: code=" + result.response().code() + ", message=" + result.response().message());
            Log.d("Retrofit(mock)", "Content: " + new Gson().toJson(result.response()));
        } catch(Exception ignored) {}
    }

    private static void printRequestEnd(StackTraceElement[] elements) {
        Pair<String, String> pair = getCallerAndRequest(elements);
        try {
            Log.d("Retrofit(mock)", "<-- launch from " + pair.first + "<-- " + pair.second + ": END");
        } catch(Exception ignored) {}
    }

    private static Pair<String, String> getCallerAndRequest(StackTraceElement[] elements) {
        String requestName = null;
        String uiCallerName = null;
        String dataCallerName = null;

        for(StackTraceElement element : elements) {
            if(requestName == null && element.getClassName().contains("PaginationDataSource")) {
                requestName = element.getMethodName();
            }
            if(uiCallerName == null && element.getClassName().contains(".ui.")) {
                uiCallerName = element.getFileName();
            }
            if(dataCallerName == null && element.getClassName().contains("DataManager")) {
                dataCallerName = element.getFileName();
            }
        }

        if(requestName == null) {
            requestName = "UNKNOWN";
        }

        if(uiCallerName == null) {
            if(dataCallerName != null) {
                uiCallerName = dataCallerName;
            }
            else {
                uiCallerName = "UNKNOWN";
            }
        }

        return new Pair<>(uiCallerName.replace(".java", ""), requestName);
    }

}
