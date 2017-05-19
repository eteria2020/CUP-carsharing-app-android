package it.sharengo.eteria.ui.base.activities;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import it.sharengo.eteria.R;

public abstract class BaseNoNavigationActivity extends BaseToolbarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.activity_container);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rootView.addView(mToolBarView, params);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
