package it.sharengo.eteria.ui.splash;

import android.content.Intent;
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



    protected Bundle getIntentExtras(){
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                return intent.getExtras(); // Handle text being sent
            }

        }
        return null;
    }
}
