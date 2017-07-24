package it.sharengo.development.routing;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import it.sharengo.development.data.models.Feed;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.ui.assistance.AssistanceActivity;
import it.sharengo.development.ui.base.activities.BaseDrawerActivity;
import it.sharengo.development.ui.chronology.ChronologyActivity;
import it.sharengo.development.ui.feeds.FeedsActivity;
import it.sharengo.development.ui.feedsdetail.FeedsDetailActivity;
import it.sharengo.development.ui.home.HomeActivity;
import it.sharengo.development.ui.login.LoginActivity;
import it.sharengo.development.ui.longintro.LongIntroActivity;
import it.sharengo.development.ui.map.MapActivity;
import it.sharengo.development.ui.onboarding.OnboardingActivity;
import it.sharengo.development.ui.passwordrecovery.PasswordRecoveryActivity;
import it.sharengo.development.ui.profile.ProfileActivity;
import it.sharengo.development.ui.settingcities.SettingsCitiesActivity;
import it.sharengo.development.ui.settings.SettingsActivity;
import it.sharengo.development.ui.settingsaddresses.SettingsAddressesActivity;
import it.sharengo.development.ui.settingsaddressesnew.SettingsAddressesNewActivity;
import it.sharengo.development.ui.settingslang.SettingsLangActivity;
import it.sharengo.development.ui.share.ShareActivity;
import it.sharengo.development.ui.shortintro.ShortIntroActivity;
import it.sharengo.development.ui.signup.SignupActivity;
import it.sharengo.development.ui.slideshow.SlideshowActivity;
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

    public static final String EXTRA_LOGIN = "EXTRA_LOGIN";
    public static final String EXTRA_FEEDS = "EXTRA_FEEDS";
    public static final String EXTRA_MAP = "EXTRA_MAPA";

    public static final int REQUEST_LOGIN_START = 1;
    public static final int REQUEST_LOGIN_PROFILE = 2;
    public static final int REQUEST_LOGIN_MAPS = 3;
    public static final int REQUEST_LOGIN_FEEDS = 4;

    public static final int REQUEST_MAP_DEFAULT = 1;
    public static final int REQUEST_MAP_FEEDS = 2;

    private Navigator() {
    }

    public static void launchHome(Activity activity) {
        Intent intent = HomeActivity.getCallingIntent(activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        activity.startActivity(intent);
    }

    public static void launchHome(Fragment fragment) {
        Intent intent = HomeActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchLogin(Fragment fragment, int type) {
        Intent intent = LoginActivity.getCallingIntent(fragment.getActivity(), type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        intent.putExtra(EXTRA_LOGIN, type);
        fragment.startActivity(intent);
    }

    public static void launchMap(Fragment fragment, int type) {
        Intent intent = MapActivity.getCallingIntent(fragment.getActivity(), type);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchTripEnd(Fragment fragment, float co2) {
        Intent intent = TripEndActivity.getCallingIntent(fragment.getActivity(), co2);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
        fragment.getActivity().finish();
    }

    public static void launchProfile(Fragment fragment) {
        Intent intent = ProfileActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchSettings(Fragment fragment) {
        Intent intent = SettingsActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchSettings(Activity activity) {
        Intent intent = SettingsActivity.getCallingIntent(activity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        activity.startActivity(intent);
    }

    public static void launchSettingsCities(Fragment fragment, boolean feeds) {
        Intent intent = SettingsCitiesActivity.getCallingIntent(fragment.getActivity(), feeds);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchSettingsAddresses(Fragment fragment) {
        Intent intent = SettingsAddressesActivity.getCallingIntent(fragment.getActivity());
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchSettingsAddressesNew(Fragment fragment) {
        Intent intent = SettingsAddressesNewActivity.getCallingIntent(fragment.getActivity());
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchSettingsLang(Fragment fragment) {
        Intent intent = SettingsLangActivity.getCallingIntent(fragment.getActivity());
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchSlideshow(Fragment fragment) {
        Intent intent = SlideshowActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchPasswordRecovery(Fragment fragment) {
        Intent intent = PasswordRecoveryActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchSignup(Fragment fragment) {
        Intent intent = SignupActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchLongIntro(Fragment fragment) {
        Intent intent = LongIntroActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchShortIntro(Fragment fragment) {
        Intent intent = ShortIntroActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchChronology(Fragment fragment) {
        Intent intent = ChronologyActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchOnboarding(Fragment fragment) {
        Intent intent = OnboardingActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchFeeds(Fragment fragment, String category_id, String category_name) {
        Intent intent = FeedsActivity.getCallingIntent(fragment.getActivity(), category_id, category_name);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchFeedsDetail(Fragment fragment, Feed feed) {
        Intent intent = FeedsDetailActivity.getCallingIntent(fragment.getActivity(), feed);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchAssistance(Fragment fragment) {
        Intent intent = AssistanceActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchShare(Fragment fragment) {
        Intent intent = ShareActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }
    
}
