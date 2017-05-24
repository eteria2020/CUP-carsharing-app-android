package it.sharengo.development.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import it.sharengo.development.ui.base.activities.BaseActivity;

/*public class HomeActivity extends BaseDrawerActivity {

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            replaceFragment(HomeFragment.newInstance());
        }
        
        //setToolbarTitle(getString(R.string.app_name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuToUse = R.menu.right_side_menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(menuToUse, menu);

        return super.onCreateOptionsMenu(menu);
    }
}*/

public class HomeActivity extends BaseActivity {

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            replaceFragment(HomeFragment.newInstance());
        }
    }
}
