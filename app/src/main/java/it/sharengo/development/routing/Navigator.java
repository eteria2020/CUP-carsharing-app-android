package it.sharengo.development.routing;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.ui.base.activities.BaseDrawerActivity;
import it.sharengo.development.ui.home.HomeActivity;
import it.sharengo.development.ui.map.MapActivity;
import it.sharengo.development.ui.tripend.TripEndActivity;

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

    public static void launchHome(Activity activity) {
        Intent intent = HomeActivity.getCallingIntent(activity);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        activity.startActivity(intent);
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

    public static void launchTripEnd(Fragment fragment, float co2) {
        Intent intent = TripEndActivity.getCallingIntent(fragment.getActivity(), co2);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);

        fragment.getActivity().finish();
    }

    
}
