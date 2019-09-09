package it.sharengo.eteria.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.onesignal.OneSignal;

import it.sharengo.eteria.BuildConfig;
import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.activities.BaseActivity;

public class SplashActivity extends BaseActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            replaceFragment(SplashFragment.newInstance());
        }
        OneSignal.sendTag("server", getString(R.string.defLang));
        if(BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug"))
            OneSignal.sendTag("debug", "true");
    }

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, SplashActivity.class);
        return i;
    }


    protected Bundle getIntentExtras(){
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        String referrer="";
        Bundle result = new Bundle();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1 && getReferrer() !=null) {
            referrer = getReferrer().getHost();
        }

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                result = intent.getExtras();
                result.putString("CALLING_APP",referrer);
                return result; // Handle text being sent
            }

        }else if (Intent.ACTION_VIEW.equals(action)) {
            Uri data = intent.getData();
            String plate ;
            if(data.getHost().equalsIgnoreCase("www.sharengo.it")) {


                 plate = data.getPath();
                if(plate ==null || plate.isEmpty())
                    return null;
                plate = plate.substring(plate.lastIndexOf("/")+1);
                result.putString(Intent.EXTRA_TEXT, plate.toUpperCase());
                result.putString("CALLING_APP",referrer);
                return result;
            }else if(data.getHost().equalsIgnoreCase("mobile.sharengo.it")){

                 plate = data.getQuery();
                if(plate ==null || plate.isEmpty())
                    return null;
                plate = plate.substring(plate.lastIndexOf("=")+1);
                result.putString(Intent.EXTRA_TEXT, plate.toUpperCase());
                result.putString("CALLING_APP",referrer);
                return result;
            }
        }
        return null;
    }
}
