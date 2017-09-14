package it.sharengo.eteria.ui.settingcities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;


public class SettingsCitiesActivity extends BaseDrawerActivity {

    private static final String TAG = SettingsCitiesActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context, boolean feeds) {
        Intent i = new Intent(context, SettingsCitiesActivity.class);
        i.putExtra(Navigator.EXTRA_FEEDS, feeds);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getIntent().getExtras() != null) {
            boolean feeds = getIntent().getExtras().getBoolean(Navigator.EXTRA_FEEDS);
            replaceFragment(SettingsCitiesFragment.newInstance(feeds));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuToUse = R.menu.right_side_menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuToUse, menu);

        return super.onCreateOptionsMenu(menu);
    }
/*
    @Override
    public void onBackPressed() {
        Navigator.launchSettings(this);
        super.onBackPressed();
    }*/
}
