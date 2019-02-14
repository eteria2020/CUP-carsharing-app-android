package it.sharengo.eteria.ui.genericWebView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.eteria.R;
import it.sharengo.eteria.routing.Navigator;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;
import it.sharengo.eteria.ui.base.webview.WebViewUser;

public class GenericWebViewActivity extends BaseDrawerActivity implements WebViewUser {



    private static final String TAG = GenericWebViewActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context, String url2Open) {
        Intent i = new Intent(context, GenericWebViewActivity.class);
        i.putExtra(Navigator.EXTRA_GENERIC_WEBVIEV, url2Open);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getIntent().getExtras() != null) {
            String type = getIntent().getExtras().getString(Navigator.EXTRA_GENERIC_WEBVIEV);
            replaceFragment(GenericWebViewFragment.newInstance(type));
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
    public void onBackPressed() {

        if(handleBackButton())
            super.onBackPressed();
    }

    @Override
    public boolean handleBackButton() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if(f instanceof GenericWebViewFragment){
            if(((GenericWebViewFragment) f).webview.canGoBack()){
                ((GenericWebViewFragment) f).webview.goBack();
                return false;
            }
        }
        return true;
    }
}
