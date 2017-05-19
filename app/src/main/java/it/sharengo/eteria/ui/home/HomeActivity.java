package it.sharengo.eteria.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;

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
        
        setToolbarTitle(getString(R.string.app_name));
    }
}
