package it.sharengo.eteria.ui.shortintro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;

import org.json.JSONObject;

import javax.inject.Inject;

import it.sharengo.eteria.App;
import it.sharengo.eteria.R;
import it.sharengo.eteria.data.repositories.NotificationRepository;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseActivity;


public class ShortIntroActivity extends BaseActivity {

    private static final String TAG = ShortIntroActivity.class.getSimpleName();

    @Inject
    NotificationRepository mNotificationRepository;

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, ShortIntroActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PERF","onCreate ShortIntroActivity");
        if (savedInstanceState == null) {
            replaceFragment(ShortIntroFragment.newInstance());
        }
        App.getInstance().getComponent().inject(this);
    }

    @Override
    public void finish() {

        if(mNotificationRepository.getLastNotificationData()!=null) {
            Log.d("BOMB", "finish: " + mNotificationRepository.getLastNotificationData().toString());
            JSONObject data = mNotificationRepository.getLastNotificationData();

            if (data != null) {
                //switch t to get Notification custom Type
                switch (data.optInt("t",0)){
                    case 1://Close Trip Notification
                        Navigator.launchChronology(this);
                        break;
                    case 2://Close Trip Notification
                        Navigator.launchMapGoogle(this, Navigator.REQUEST_MAP_DEFAULT);
                        break;
                    case 3://Close Trip Notification
                        String url2open= data.optString("l","");
                        Navigator.launchGenericWebView(App.getInstance().getApplicationContext(), url2open);
                        break;
                    default:
                        Navigator.launchMapGoogle(this, Navigator.REQUEST_MAP_DEFAULT);
                }
            }
            mNotificationRepository.notificationHanlded();

        }else {
            Navigator.launchMapGoogle(this, Navigator.REQUEST_MAP_DEFAULT);
        }
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_up);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

