package it.sharengo.eteria.routing;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.ui.assistance.AssistanceActivity;
import it.sharengo.eteria.ui.base.activities.BaseDrawerActivity;
import it.sharengo.eteria.ui.buyminutes.BuyMinutesActivity;
import it.sharengo.eteria.ui.chronology.ChronologyActivity;
import it.sharengo.eteria.ui.faq.FaqActivity;
import it.sharengo.eteria.ui.feeds.FeedsActivity;
import it.sharengo.eteria.ui.feedsdetail.FeedsDetailActivity;
import it.sharengo.eteria.ui.home.HomeActivity;
import it.sharengo.eteria.ui.login.LoginActivity;
import it.sharengo.eteria.ui.longintro.LongIntroActivity;
import it.sharengo.eteria.ui.map.MapActivity;
import it.sharengo.eteria.ui.mapgoogle.MapGoogleActivity;
import it.sharengo.eteria.ui.onboarding.OnboardingActivity;
import it.sharengo.eteria.ui.passwordrecovery.PasswordRecoveryActivity;
import it.sharengo.eteria.ui.profile.ProfileActivity;
import it.sharengo.eteria.ui.settingcities.SettingsCitiesActivity;
import it.sharengo.eteria.ui.settings.SettingsActivity;
import it.sharengo.eteria.ui.settingsaddresses.SettingsAddressesActivity;
import it.sharengo.eteria.ui.settingsaddressesnew.SettingsAddressesNewActivity;
import it.sharengo.eteria.ui.settingslang.SettingsLangActivity;
import it.sharengo.eteria.ui.share.ShareActivity;
import it.sharengo.eteria.ui.shortintro.ShortIntroActivity;
import it.sharengo.eteria.ui.signup.SignupActivity;
import it.sharengo.eteria.ui.slideshow.SlideshowActivity;
import it.sharengo.eteria.ui.tripend.TripEndActivity;
import it.sharengo.eteria.ui.tutorial.TutorialActivity;
import it.sharengo.eteria.ui.userarea.UserAreaActivity;

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

    public static void launchMapGoogle(Fragment fragment, int type) {
        Intent intent = MapGoogleActivity.getCallingIntent(fragment.getActivity(), type);
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

    public static void launchUserArea(Fragment fragment) {
        Intent intent = UserAreaActivity.getCallingIntent(fragment.getActivity());
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

    public static void launchFaq(Fragment fragment) {
        Intent intent = FaqActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }

    public static void launchTutorial(Fragment fragment) {
        Intent intent = TutorialActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        fragment.startActivity(intent);
    }

    public static void launchBuy(Fragment fragment) {
        Intent intent = BuyMinutesActivity.getCallingIntent(fragment.getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(BaseDrawerActivity.EXTRA_MENU_ITEM, MenuItem.Section.HOME.toString());
        fragment.startActivity(intent);
    }
    
}
