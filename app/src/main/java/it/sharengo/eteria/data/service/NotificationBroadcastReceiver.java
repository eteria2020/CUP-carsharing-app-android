package it.sharengo.eteria.data.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import javax.inject.Inject;

import it.sharengo.eteria.App;
import it.sharengo.eteria.data.repositories.NotificationRepository;
import it.sharengo.eteria.injection.ApplicationContext;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.utils.NotificationFactory;

/**
 * Created by Fulvio on 06/07/2018.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver implements OneSignal.NotificationOpenedHandler, OneSignal.NotificationReceivedHandler{

    @Inject
    @ApplicationContext
    Context mContext;

    @Inject
    NotificationFactory notificationFactory;

    @Inject
    NotificationRepository notificationRepository;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BOMB", "ricevuto intent" + intent);
        NotificationJobIntentService.enqueueWork(context, intent);
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        Log.d("PUSH", "notificationOpened:  opened notification app is " + (App.isAppForeground()?"foreground" : "background"));
        App.getInstance().getComponent().inject(this);
        if(App.isAppForeground()) {
            notificationRepository.openedNotification(result);
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey;

            if (data != null) {
                switch (data.optInt("t", 0)) {
                    case 1://Close Trip Notification
                        Navigator.launchChronology(App.getInstance().getApplicationContext());
                        notificationRepository.notificationHanlded();
                        break;
                    case 2://Open Trip Notification
                        Navigator.launchMapGoogle(App.getInstance().getApplicationContext(),Navigator.REQUEST_MAP_DEFAULT);
                        notificationRepository.notificationHanlded();
                        break;
                    default:
                        Navigator.launchSplash(App.getInstance().getApplicationContext());
                        notificationRepository.notificationHanlded();
                }

            }else {
                Navigator.launchSplash(App.getInstance().getApplicationContext());
            }
        }else{
            notificationRepository.openedNotification(result);
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey;

            if (data != null) {
                switch (data.optInt("t", 0)) {
                    case 1://Close Trip Notification
                        Navigator.launchSplash(App.getInstance().getApplicationContext());
                        //Navigator.launchChronology(App.getInstance().getApplicationContext());
                        //notificationRepository.notificationHanlded();
                        break;
                    case 2://Open Trip Notification
                        Navigator.launchSplash(App.getInstance().getApplicationContext());
                        //Navigator.launchMapGoogle(App.getInstance().getApplicationContext(),Navigator.REQUEST_MAP_DEFAULT);
                        //notificationRepository.notificationHanlded();
                        break;
                    default:
                        Navigator.launchSplash(App.getInstance().getApplicationContext());
                        notificationRepository.notificationHanlded();
                }

            }
            else {

                Navigator.launchSplash(App.getInstance().getApplicationContext());
            }
           // Navigator.launchSplash(App.getInstance().getApplicationContext());
            //notificationRepository.openedNotification(result);
        }


    }

    @Override
    public void notificationReceived(OSNotification notification) {

    }

    private Intent buildIntentFromOSNotification(OSNotificationOpenResult result){
        Intent intent = new Intent(mContext,NotificationJobIntentService.class);
        intent.setAction(NotificationFactory.ACTION_ONE_SIGNAL);
        Bundle bundle = new Bundle();
        bundle.putString(NotificationFactory.KEY_OS_ACTION_ID,result.action.actionID);
        bundle.putString(NotificationFactory.KEY_OS_ACTION_TYPE,result.action.type.name());
        bundle.putString(NotificationFactory.KEY_OS_ADDITIONAL_DATA,result.notification.payload.additionalData.toString());
        return intent;
    }
}
