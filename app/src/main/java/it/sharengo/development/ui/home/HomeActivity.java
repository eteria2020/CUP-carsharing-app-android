package it.sharengo.development.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import it.sharengo.development.R;
import it.sharengo.development.ui.base.activities.BaseDrawerActivity;

public class HomeActivity extends BaseDrawerActivity {

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            replaceFragment(HomeFragment.newInstance());
        }
        
        //setToolbarTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuToUse = R.menu.right_side_menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuToUse, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Show message of notification.
     *
     * @param  notification  string of notification received.
     * @param  mNotificationListener listener for intercept click user of notification banner.
     */
    @Override
    public void showNotification(String notification, View.OnClickListener mNotificationListener){
        Log.w("RESER","showNotification ACTIVITY");
        super.showNotification(notification, mNotificationListener);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.fade_out);
    }
}

/*public class HomeActivity extends BaseActivity {

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            replaceFragment(HomeFragment.newInstance());
        }
    }
}*/
