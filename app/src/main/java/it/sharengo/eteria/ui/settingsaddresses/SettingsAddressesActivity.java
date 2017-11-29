package it.sharengo.eteria.ui.settingsaddresses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;


public class SettingsAddressesActivity extends BaseDrawerActivity {

    private static final String TAG = SettingsAddressesActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, SettingsAddressesActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToolBar.findViewById(R.id.menuButton).setVisibility(View.GONE);
        if (savedInstanceState == null) {
            replaceFragment(SettingsAddressesFragment.newInstance());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuToUse = R.menu.right_side_menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuToUse, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
