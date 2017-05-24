package it.sharengo.development.ui.base.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import it.sharengo.development.R;

public abstract class BaseBackActivity extends BaseToolbarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
