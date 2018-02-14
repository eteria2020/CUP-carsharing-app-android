package it.sharengo.eteria.ui.splash;

import android.content.Intent;
import android.net.Uri;
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

        }else if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            String plate ;
            if(data.getHost().equalsIgnoreCase("www.sharengo.it")) {


                 plate = data.getPath();
                if(plate ==null || plate.isEmpty())
                    return null;
                plate = plate.substring(plate.lastIndexOf("/")+1);
                Bundle result = new Bundle();
                result.putString(Intent.EXTRA_TEXT, plate.toUpperCase());
                return result;
            }else if(data.getHost().equalsIgnoreCase("mobile.sharengo.it")){

                 plate = data.getQuery();
                if(plate ==null || plate.isEmpty())
                    return null;
                plate = plate.substring(plate.lastIndexOf("=")+1);
                Bundle result = new Bundle();
                result.putString(Intent.EXTRA_TEXT, plate.toUpperCase());
                return result;
            }
        }
        return null;
    }
}
