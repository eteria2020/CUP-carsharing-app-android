package it.sharengo.development.ui.map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.development.R;
import it.sharengo.development.ui.base.activities.BaseDrawerActivity;


public class MapActivity extends BaseDrawerActivity {

    private static final String TAG = MapActivity.class.getSimpleName();

    private MapFragment mapFragment;

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, MapActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mapFragment = MapFragment.newInstance();
            replaceFragment(mapFragment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuToUse = R.menu.right_side_menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuToUse, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void actionBarInteraction(){
        super.actionBarInteraction();
        finish();
        if(mapFragment != null){
            mapFragment.onClosePopup();
        }
    }

    public void redrawFragment(){
        replaceFragment(MapFragment.newInstance());
    }

}
