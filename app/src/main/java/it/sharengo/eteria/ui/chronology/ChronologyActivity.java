package it.sharengo.eteria.ui.chronology;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;


public class ChronologyActivity extends BaseDrawerActivity {

    private static final String TAG = ChronologyActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, ChronologyActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            replaceFragment(ChronologyFragment.newInstance());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuToUse = R.menu.right_side_menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuToUse, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out_slow);
    }
}
