package it.sharengo.development.ui.base.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import it.handroix.core.base.HdxBaseActivity;
import it.sharengo.development.R;

public abstract class BaseActivity extends HdxBaseActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frame);
        setupLoadingFragment(android.R.id.content);
    }

    public void showLoading() {
        Log.w("SHOW","LOADING");
        super.showLoading();
    }

    public void hideLoading() {
        super.hideLoading();
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
