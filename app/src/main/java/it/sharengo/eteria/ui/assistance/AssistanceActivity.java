package it.sharengo.eteria.ui.assistance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import it.sharengo.eteria.App;
import it.sharengo.eteria.R;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;
import it.sharengo.eteria.utils.MyContextWrapper;

public class AssistanceActivity extends BaseDrawerActivity {

    private static final String TAG = AssistanceActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context) {
        Intent i = new Intent(context, AssistanceActivity.class);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            replaceFragment(AssistanceFragment.newInstance());
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, App.getInstance().mLang));
    }
}
