package it.sharengo.development.ui.tripend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.development.R;
import it.sharengo.development.ui.base.activities.BaseDrawerActivity;


public class TripEndActivity extends BaseDrawerActivity {

    private static final String TAG = TripEndActivity.class.getSimpleName();

    private static final String EXTRA_TRIPEND_TIMESTAMP = "EXTRA_TRIPEND_TIMESTAMP";

    public static Intent getCallingIntent(Context context, int timestamp) {
        Intent i = new Intent(context, TripEndActivity.class);
        i.putExtra(EXTRA_TRIPEND_TIMESTAMP, timestamp);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getIntent().getExtras() != null) {
            int timestamp = getIntent().getExtras().getInt(EXTRA_TRIPEND_TIMESTAMP);
            replaceFragment(TripEndFragment.newInstance(timestamp));
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
