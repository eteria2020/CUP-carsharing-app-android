package it.sharengo.eteria.ui.legalnote;

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


public class LegalNoteActivity extends BaseDrawerActivity implements WebViewUser {

    public enum InnerRoute{
        PRIVACY,
        LEGAL_NOTE
    }

    private static final String TAG = LegalNoteActivity.class.getSimpleName();

    public static Intent getCallingIntent(Context context, InnerRoute route) {
        Intent i = new Intent(context, LegalNoteActivity.class);

        i.putExtra(Navigator.EXTRA_LEGAL_STATEMET, route.name());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getIntent().getExtras() != null) {
            String type = getIntent().getExtras().getString(Navigator.EXTRA_LEGAL_STATEMET);
            replaceFragment(LegalNoteFragment.newInstance(LegalNoteActivity.InnerRoute.valueOf(type)));
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
        if(f instanceof LegalNoteFragment){
            if(((LegalNoteFragment) f).webview.canGoBack()){
                ((LegalNoteFragment) f).webview.goBack();
                return false;
            }
        }
        return true;
    }
}
