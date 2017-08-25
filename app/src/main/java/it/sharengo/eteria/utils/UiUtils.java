package it.sharengo.development.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import it.sharengo.development.R;
import it.sharengo.development.data.common.ErrorResponse;

public class UiUtils {

    public static void showGenericErrorMsg(Activity activity) {
        UiUtils.showErrorMsg(activity, activity.getString(R.string.error_generic_msg));
    }

    public static void showInfoMsg(Activity activity, String infoMessage) {
        Snackbar snackbar = Snackbar.make(
                activity.findViewById(R.id.content_frame),
                infoMessage,
                Snackbar.LENGTH_LONG
        );
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ActivityCompat.getColor(activity, R.color.successColor));
        snackbar.show();
    }

    public static void showErrorMsg(Activity activity, String errorMessage) {
        Log.w("errorMessage",": "+errorMessage);
        Snackbar snackbar = Snackbar.make(
                activity.findViewById(R.id.content_frame),
                errorMessage,
                Snackbar.LENGTH_LONG
        );
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ActivityCompat.getColor(activity, R.color.failureColor));
        snackbar.show();
    }

    public static void showErrorMsg(Activity activity, Throwable throwable) {
        
        if(!(throwable instanceof ErrorResponse)) {
            showErrorMsg(activity, throwable.getMessage());
            return;
        }
        
        ErrorResponse errorResponse = (ErrorResponse) throwable;

        switch (errorResponse.errorType){
            case CUSTOM:
                showErrorMsg(activity, errorResponse.rawMessage);
                break;
            case NO_NETWORK:
                showErrorMsg(activity, activity.getString(R.string.error_msg_no_network));
                break;
            case SERVER_TIMEOUT:
                showErrorMsg(activity, activity.getString(R.string.error_msg_server_timeout));
                break;
            case CONVERSION:
                showErrorMsg(activity, activity.getString(R.string.error_msg_conversion));
                break;
            case HTTP:
                /*
                 * rawMessage may contain json or structured objects
                 * this need to be customized in your project
                 */

                if(!TextUtils.isEmpty(errorResponse.rawMessage)){
                    showErrorMsg(activity, errorResponse.rawMessage);
                }else {
                    showErrorByHttpStatus(activity, errorResponse.httpStatus);
                }

                break;
            case UNEXPECTED:
                showErrorMsg(activity, activity.getString(R.string.error_unexpected));
                break;
        }
    }

    private static void showErrorByHttpStatus(Activity activity, int httpStatus){
        switch (httpStatus){
            case 400:
                showErrorMsg(activity, activity.getString(R.string.error_msg_http_400));
                break;
            case 401:
                showErrorMsg(activity, activity.getString(R.string.error_msg_http_401));
                break;
            case 403:
                showErrorMsg(activity, activity.getString(R.string.error_msg_http_403));
                break;
            case 404:
                showErrorMsg(activity, activity.getString(R.string.error_msg_http_404));
                break;
            case 406:
                showErrorMsg(activity, activity.getString(R.string.error_msg_http_406));
                break;
            case 500:
                showErrorMsg(activity, activity.getString(R.string.error_msg_http_500));
                break;
        }
    }

}
