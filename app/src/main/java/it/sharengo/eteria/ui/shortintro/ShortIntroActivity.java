package it.sharengo.eteria.ui.shortintro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.activities.BaseActivity;


public class ShortIntroActivity extends BaseActivity {

    private static final String TAG = ShortIntroActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, ShortIntroActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            replaceFragment(ShortIntroFragment.newInstance());
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_up);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
