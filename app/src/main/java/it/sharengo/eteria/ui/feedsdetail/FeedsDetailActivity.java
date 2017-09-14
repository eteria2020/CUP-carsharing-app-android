package it.sharengo.eteria.ui.feedsdetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;


public class FeedsDetailActivity extends BaseDrawerActivity {

    private static final String TAG = FeedsDetailActivity.class.getSimpleName();

    private static final String EXTRA_FEED = "EXTRA_FEED";

    public static Intent getCallingIntent(Context context, Feed feed) {
        Intent i = new Intent(context, FeedsDetailActivity.class);
        i.putExtra(EXTRA_FEED, feed);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Feed feed = (Feed) getIntent().getExtras().getSerializable(EXTRA_FEED);
            replaceFragment(FeedsDetailFragment.newInstance(feed));
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
