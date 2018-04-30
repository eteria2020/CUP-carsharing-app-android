package it.sharengo.eteria.ui.userarea;

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
import it.sharengo.eteria.ui.faq.FaqFragment;


public class UserAreaActivity extends BaseDrawerActivity implements WebViewUser {

    public enum InnerRoute{
        HOME,
        PAYMENTS,
        DRIVER_LICENSE,
        RATES
    }

    private static final String TAG = UserAreaActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context, InnerRoute route) {
        Intent i = new Intent(context, UserAreaActivity.class);

        i.putExtra(Navigator.EXTRA_USER_AREA, route.name());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getIntent().getExtras() != null) {
            String type = getIntent().getExtras().getString(Navigator.EXTRA_USER_AREA);
            replaceFragment(UserAreaFragment.newInstance(UserAreaActivity.InnerRoute.valueOf(type)));
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
        if(f instanceof UserAreaFragment){
            if(((UserAreaFragment) f).webview.canGoBack()){
                ((UserAreaFragment) f).webview.goBack();
                return false;
            }
        }
        return true;
    }
}
