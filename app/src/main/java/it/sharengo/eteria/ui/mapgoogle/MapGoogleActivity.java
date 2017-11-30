package it.sharengo.eteria.ui.mapgoogle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;


public class MapGoogleActivity extends BaseDrawerActivity {

    private static final String TAG = MapGoogleActivity.class.getSimpleName();

    private MapGoogleFragment mapFragment;

    public static Intent getCallingIntent(Context context, int type) {
        Intent i = new Intent(context, MapGoogleActivity.class);
        i.putExtra(Navigator.EXTRA_MAP, type);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToolBar.findViewById(R.id.menuButton).setVisibility(View.GONE);
        if (savedInstanceState == null && getIntent().getExtras() != null) {
            int type = getIntent().getExtras().getInt(Navigator.EXTRA_MAP);
            replaceFragment(MapGoogleFragment.newInstance(type));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuToUse = R.menu.right_side_menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuToUse, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Intercept if user press any button of action bar.
     */
    @Override
    public void actionBarInteraction(){
        super.actionBarInteraction();

        if(mapFragment != null){
            mapFragment.onClosePopup();
        }
    }

    /**
     * Show message of notification.
     *
     * @param  notification  string of notification received.
     * @param  mNotificationListener listener for intercept click user of notification banner.
     */
    @Override
    public void showNotification(String notification, View.OnClickListener mNotificationListener){

        super.showNotification(notification, mNotificationListener);
    }
}