package it.sharengo.eteria.ui.splash;

import android.os.Bundle;

import it.sharengo.eteria.ui.base.activities.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            replaceFragment(SplashFragment.newInstance());
        }
    }
}
