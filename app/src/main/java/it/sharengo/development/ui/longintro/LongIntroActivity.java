package it.sharengo.development.ui.longintro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import it.sharengo.development.R;
import it.sharengo.development.ui.base.activities.BaseActivity;


public class LongIntroActivity extends BaseActivity {

    private static final String TAG = LongIntroActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, LongIntroActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            replaceFragment(LongIntroFragment.newInstance());
        }
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(0, R.anim.slide_out_up);
    }

}
