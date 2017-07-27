package it.sharengo.development.ui.mapgoogle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import it.sharengo.development.ui.base.activities.BaseDrawerActivity;


public class MapGoogleActivity extends BaseDrawerActivity {

    private static final String TAG = MapGoogleActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, MapGoogleActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            replaceFragment(MapGoogleFragment.newInstance());
        }
    }

}
