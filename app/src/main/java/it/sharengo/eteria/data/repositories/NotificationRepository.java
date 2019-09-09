package it.sharengo.eteria.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.onesignal.OSNotificationOpenResult;

import org.json.JSONObject;

import javax.inject.Inject;

import it.sharengo.eteria.R;
import it.sharengo.eteria.injection.ApplicationContext;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Fulvio on 24/08/2018.
 */

public class NotificationRepository {

    public static final String TAG = NotificationRepository.class.getSimpleName();

    private Context mContext;

    private static final String PREF_KEY_NOTIFICATION = "last_notification_json";

    private JSONObject notificationData = null;

    @Inject
    public NotificationRepository(@ApplicationContext Context context) {
        mContext = context;
    }

    public void openedNotification(OSNotificationOpenResult result){
        if(result!=null){
            createOrUpdateNotificationInMemory(result.toJSONObject());
        }
    }


    private void createOrUpdateNotificationInMemory(@NonNull JSONObject result) {
        JSONObject data = new JSONObject();
        try {
            JSONObject notification = result.optJSONObject("notification");
            if(notification!=null){
                JSONObject payload = notification.optJSONObject("payload");
                if(payload !=null){
                    data = payload.optJSONObject("additionalData");
                }
            }
        }catch (Exception e){
            Log.e("PUSH", "createOrUpdateNotificationInMemory: error in json parse", e);
        }
        notificationData = data;
        if(data!= null) {
            Log.d("PUSH", "createOrUpdateNotificationInMemory: " + data.toString());
            SharedPreferences mPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), MODE_PRIVATE);
            Gson gson = new Gson();
            mPref.edit()
                    .putString(PREF_KEY_NOTIFICATION, data.toString())
                    .apply();
        }

    }

    public @Nullable JSONObject getLastNotificationData() {
        if(notificationData == null)
            notificationData = getPreferencesNotificationData();

        return notificationData;
    }

    private @Nullable JSONObject getPreferencesNotificationData(){
        SharedPreferences mPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), MODE_PRIVATE);

        String notificationJson = mPref.getString(PREF_KEY_NOTIFICATION,"");
        if(!notificationJson.isEmpty()){
            try {
                return new JSONObject(notificationJson);
            }catch (Exception e) {
                Log.d("IVAN", "gson",e);
                return null;
            }
        }
        return null;
    }

    public void notificationHanlded(){
        SharedPreferences mPref = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), MODE_PRIVATE);
        mPref.edit().remove(PREF_KEY_NOTIFICATION).apply();
        notificationData = null;
    }
}
