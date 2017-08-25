package it.sharengo.development.ui.splash;

import android.os.Bundle;

import it.sharengo.development.ui.base.activities.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            replaceFragment(SplashFragment.newInstance());
        }
    }
}
