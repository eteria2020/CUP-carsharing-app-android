package it.sharengo.eteria.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;


import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.eteria.BuildConfig;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.service.NotificationBroadcastReceiver;
import it.sharengo.eteria.injection.ApplicationContext;

/**
 * Created by Fulvio on 03/07/2018.
 */

@Singleton
public class NotificationFactory {
    private static final String TAG = NotificationFactory.class.getSimpleName();

    public static final String KEY_REMOTE_TEXT = "key_remoteText";
    public static final String KEY_OS_ACTION_ID = "key_os_action_id";
    public static final String KEY_OS_PLATE = "plate";
    public static final String KEY_OS_ACTION_TYPE = "key_os_action_type";
    public static final String KEY_OS_ADDITIONAL_DATA = "key_os_additional_data";

    public static final String ACTION_SELECT_CAR = "action_select_car";
    public static final String ACTION_ONE_SIGNAL = "action_push_notification";
    public static final String ACTION_REMOTE_INPUT = "action_remote_input";

    private static final String DEFAULT_CHANNEL = "Default";
    private static final String PRIORITY_CHANNEL = "Prioritarie";

    public static final int TEST_NOTIFICATION_ID = -1;

    final Context mContext;
    public static final int ENTER_GEOFENCE_ID = 0;

    final NotificationHandler handler = new NotificationHandler(this);

    @Inject
    public NotificationFactory(@ApplicationContext Context context) {
        mContext = context;
    }

    public void makeTestNotification() {
        makeSimpleNotification("Notifica di test", "Corpo della notifica su una riga", TEST_NOTIFICATION_ID);
    }

    public void makeSimpleNotification(String title, String body, int notificationId) {
        Bundle bundle = new Bundle();
        bundle.putString("plate","LITEST");
        PendingIntent contentIntent = getBasePendingIntent(ACTION_SELECT_CAR,bundle);

        NotificationCompat.Builder b = new NotificationCompat.Builder(mContext, DEFAULT_CHANNEL);
        b.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setFullScreenIntent(contentIntent, true)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        sendNotification(b.build(), DEFAULT_CHANNEL, notificationId);
    }
    public void makeDataNotification(String title, String body, int notificationId, String action, Bundle extra) {
        PendingIntent contentIntent = getBasePendingIntent(action,extra);

        NotificationCompat.Builder b = new NotificationCompat.Builder(mContext, DEFAULT_CHANNEL);
        b.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setFullScreenIntent(contentIntent, true)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        sendNotification(b.build(), DEFAULT_CHANNEL, notificationId);
    }

    public void makeTextRequest(String title, String body, int notificationId) {

        NotificationCompat.Builder b = new NotificationCompat.Builder(mContext, PRIORITY_CHANNEL);
        b.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .addAction(getInputAction(notificationId))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setAutoCancel(true);

        sendNotification(b.build(), PRIORITY_CHANNEL, notificationId);
    }

    public void makeActionRequest(String title, String body, int notificationId,PendingIntent firstAction, String firstLabel, PendingIntent secondAction, String secondLabel) {
        NotificationCompat.Builder b = getStandardBuilder(PRIORITY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .addAction(new NotificationCompat.Action(R.drawable.ic_arrow_drop_up, firstLabel, firstAction))
                .addAction(new NotificationCompat.Action(R.drawable.ic_close, secondLabel, secondAction));

        sendNotification(b.build(), PRIORITY_CHANNEL, notificationId);
    }



    public void dismissNotification(int notifyId, String dismissText) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, PRIORITY_CHANNEL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(dismissText);
        notificationManager.notify(notifyId, builder.build());
        handler.closeNotificationAfter(notifyId,2000);

    }

    private void cancelNotification(int notificationId){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.cancel(notificationId);
    }

    private NotificationCompat.Builder getStandardBuilder(String channel) {
        return new NotificationCompat.Builder(mContext, channel)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setCategory(Notification.CATEGORY_MESSAGE);

    }






    private PendingIntent getRemoteInputPengingIntent(int id) {
        Bundle b = new Bundle();
        b.putInt(KEY_REMOTE_TEXT, id);
        return getBasePendingIntent(ACTION_REMOTE_INPUT, b);
    }

    private PendingIntent getBasePendingIntent(String action, Bundle extras) {
        Intent intent = new Intent(mContext, NotificationBroadcastReceiver.class);
        intent.setAction(action);
        if (extras != null)
            intent.putExtras(extras);
        return PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Action getInputAction(int timeId) {
        return new NotificationCompat.Action.Builder(R.drawable.ic_close,
                "Aggiungi Nome", getRemoteInputPengingIntent(timeId))
                .addRemoteInput(getRemoteInput())
                .build();
    }


    private RemoteInput getRemoteInput() {
        return new RemoteInput.Builder(KEY_REMOTE_TEXT)
                .setLabel("Nome")
                .build();
    }

    private void sendNotification(@NonNull Notification notification) {
        sendNotification(notification, DEFAULT_CHANNEL, TEST_NOTIFICATION_ID);
    }

    private void sendNotification(@NonNull Notification notification, String channelName, int notificationId) {
        try {
            createNotificationChannel(channelName);
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null)
                notificationManager.notify(notificationId, notification);
        } catch (Exception e) {
            Log.e(TAG, "unknownException", e);
        }
    }


    private void createNotificationChannel(String channelName) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channelName;
            String description = "Canale mio e di nessun altro muhahah";
            int importance = PRIORITY_CHANNEL.equalsIgnoreCase(channelName) ? NotificationManager.IMPORTANCE_HIGH : NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelName, name, importance);
            channel.enableVibration(true);
            channel.setDescription(description);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    static class NotificationHandler extends Handler {
        NotificationFactory notificationFactory;

        public NotificationHandler(NotificationFactory notificationFactory) {
            this.notificationFactory = notificationFactory;
        }

        public static final int MSG_CLOSE_NOTIFICATION = 1;
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MSG_CLOSE_NOTIFICATION:
                    notificationFactory.cancelNotification(msg.arg1);

            }
        }

        public void closeNotificationAfter(int id, long milliDelay){
            Message m = Message.obtain();
            m.what = MSG_CLOSE_NOTIFICATION;
            m.arg1 = id;
            sendMessageDelayed(m,milliDelay);
        }
    }
}
