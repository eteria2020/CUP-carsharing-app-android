package it.sharengo.development.routing;

import android.content.Intent;
import android.support.v4.app.Fragment;

import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.ui.base.activities.BaseDrawerActivity;
import it.sharengo.development.ui.home.HomeActivity;
import it.sharengo.development.ui.map.MapActivity;

public class Navigator {

    /**
     * INSERT HERE REQUEST CODES FOR ACTIVITY RESULT
     * OR INTENT CONTENTS KEYS
     * 
     * Ex: public static final int REQUEST_TASK_EDIT = 1;
     * 
     * OR
     * 
     * Ex: public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
     */

    private Navigator() {
    }
    
    public static void launchHome(Fragment fragment) {
        Intent intent = HomeActivity.getCallingIntent(fragment.getActivity());
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchMap(Fragment fragment) {
        Intent intent = MapActivity.getCallingIntent(fragment.getActivity());
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    
}
