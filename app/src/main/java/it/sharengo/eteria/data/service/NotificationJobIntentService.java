package it.sharengo.eteria.data.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.RemoteInput;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.JsonObject;
import com.onesignal.OSNotificationAction;

import org.json.JSONObject;

import javax.inject.Inject;

import it.sharengo.eteria.App;
import it.sharengo.eteria.injection.ApplicationContext;
import it.sharengo.eteria.ui.splash.SplashActivity;
import it.sharengo.eteria.utils.NotificationFactory;


/**
 * Created by Fulvio on 06/07/2018.
 */

public class NotificationJobIntentService extends JobIntentService {

    private static final int JOB_ID = 573;
    private static final String TAG = "BOMB";

    @Inject
    NotificationFactory notificationFactory;

    @Inject
    @ApplicationContext
    Context mContext;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, NotificationJobIntentService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.getInstance().getComponent().inject(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        try {
            assert intent.getAction() != null;
            switch (intent.getAction()){
                case NotificationFactory.ACTION_SELECT_CAR:
                    Intent in = new Intent(this, SplashActivity.class);
                    Bundle b = intent.getExtras();
                    if(b!=null)
                        in.putExtras(b);
                    startActivity(in);
                    break;
                case NotificationFactory.ACTION_REMOTE_INPUT:
                    break;
                case NotificationFactory.ACTION_ONE_SIGNAL:
                    handleOSNotification(intent);
                    break;
            }
        }catch (Exception e) {
            Log.e(TAG, "unknownException", e);
        }
    }

    private boolean handleOSNotification(Intent intent){
        Bundle b = intent.getExtras();
        if(b==null) {
            Log.e(TAG, "handleOSNotification: received null extras");
            return false;
        }

        String type = b.getString(NotificationFactory.KEY_OS_ACTION_TYPE,"");
        if(type.equalsIgnoreCase(OSNotificationAction.ActionType.Opened.name())){
            String data = b.getString(NotificationFactory.KEY_OS_ADDITIONAL_DATA,"");
            if(data.isEmpty()){
                Log.e(TAG, "handleOSNotification: no additionalData provided");
                return false;
            }
            try {
                JSONObject obj = new JSONObject(data);
                Toast.makeText(mContext,obj.optString(NotificationFactory.KEY_OS_ACTION_ID,"cacca"),Toast.LENGTH_LONG).show();
            }catch (Exception e) {
                Log.e(TAG, "handleOSNotification: Exception in json additional data", e);
            }
        }

        return true;
    }
}
