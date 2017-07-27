package it.sharengo.development.ui.tutorial;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import it.sharengo.development.ui.base.activities.BaseActivity;


public class TutorialActivity extends BaseActivity {

    private static final String TAG = TutorialActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, TutorialActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            replaceFragment(TutorialFragment.newInstance());
        }
    }

}
