package it.sharengo.eteria.ui.faq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;
import it.sharengo.eteria.ui.base.webview.WebViewUser;


public class FaqActivity extends BaseDrawerActivity implements WebViewUser{

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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out_slow);
    }

    @Override
    public void onBackPressed() {

        if(handleBackButton())
            super.onBackPressed();
    }

    @Override
    public boolean handleBackButton() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(f instanceof FaqFragment){
            if(((FaqFragment) f).webview.canGoBack()){
                ((FaqFragment) f).webview.goBack();
                return false;
            }
        }
        return true;
    }
}
