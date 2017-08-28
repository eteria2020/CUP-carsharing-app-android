package it.sharengo.eteria.ui.faq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;


public class FaqActivity extends BaseDrawerActivity {

    private static final String TAG = FaqActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, FaqActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            replaceFragment(FaqFragment.newInstance());
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
