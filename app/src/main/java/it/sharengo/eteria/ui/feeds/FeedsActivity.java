package it.sharengo.eteria.ui.feeds;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;


public class FeedsActivity extends BaseDrawerActivity {

    private static final String TAG = FeedsActivity.class.getSimpleName();

    private static final String EXTRA_FEED_CATEGORY = "EXTRA_FEED_CATEGORY";
    private static final String EXTRA_FEED_CATEGORY_NAME = "EXTRA_FEED_CATEGORY_NAME";

    public static Intent getCallingIntent(Context context, String category_id, String category_name) {
        Intent i = new Intent(context, FeedsActivity.class);
        i.putExtra(EXTRA_FEED_CATEGORY, category_id);
        i.putExtra(EXTRA_FEED_CATEGORY_NAME, category_name);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            String category_id = getIntent().getExtras().getString(EXTRA_FEED_CATEGORY);
            String category_name = getIntent().getExtras().getString(EXTRA_FEED_CATEGORY_NAME);
            replaceFragment(FeedsFragment.newInstance(category_id, category_name));
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
