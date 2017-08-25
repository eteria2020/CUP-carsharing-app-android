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

    private static final String EXTRA_TRIPEND_CO2 = "EXTRA_TRIPEND_CO2";

    public static Intent getCallingIntent(Context context, float co2) {
        Intent i = new Intent(context, TripEndActivity.class);
        i.putExtra(EXTRA_TRIPEND_CO2, co2);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getIntent().getExtras() != null) {
            float co2 = getIntent().getExtras().getFloat(EXTRA_TRIPEND_CO2);
            replaceFragment(TripEndFragment.newInstance(co2));
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
