package it.sharengo.development.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.development.R;
import it.sharengo.development.routing.Navigator;
import it.sharengo.development.ui.base.activities.BaseDrawerActivity;


public class LoginActivity extends BaseDrawerActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context, int type) {
        Intent i = new Intent(context, LoginActivity.class);
        i.putExtra(Navigator.EXTRA_LOGIN, type);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null && getIntent().getExtras() != null) {
            int type = getIntent().getExtras().getInt(Navigator.EXTRA_LOGIN);
            replaceFragment(LoginFragment.newInstance(type));
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
        overridePendingTransition(0, R.anim.fade_out);
    }

}
