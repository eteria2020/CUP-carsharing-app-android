package it.sharengo.development.injection.components;

import dagger.Component;
import it.sharengo.development.injection.PerActivity;
import it.sharengo.development.injection.modules.MvpFragmentModule;
import it.sharengo.development.ui.assistance.AssistanceFragment;
import it.sharengo.development.ui.chronology.ChronologyFragment;
import it.sharengo.development.ui.faq.FaqFragment;
import it.sharengo.development.ui.feeds.FeedsFragment;
import it.sharengo.development.ui.feedsdetail.FeedsDetailFragment;
import it.sharengo.development.ui.login.LoginFragment;
import it.sharengo.development.ui.longintro.LongIntroFragment;
import it.sharengo.development.ui.map.MapFragment;
import it.sharengo.development.ui.home.HomeFragment;
import it.sharengo.development.ui.mapgoogle.MapGoogleFragment;
import it.sharengo.development.ui.menu.MenuFragment;
import it.sharengo.development.ui.onboarding.OnboardingFragment;
import it.sharengo.development.ui.passwordrecovery.PasswordRecoveryFragment;
import it.sharengo.development.ui.profile.ProfileFragment;
import it.sharengo.development.ui.settingcities.SettingsCitiesFragment;
import it.sharengo.development.ui.settings.SettingsFragment;
import it.sharengo.development.ui.settingsaddresses.SettingsAddressesFragment;
import it.sharengo.development.ui.settingsaddressesnew.SettingsAddressesNewFragment;
import it.sharengo.development.ui.settingslang.SettingsLangFragment;
import it.sharengo.development.ui.share.ShareFragment;
import it.sharengo.development.ui.shortintro.ShortIntroFragment;
import it.sharengo.development.ui.signup.SignupFragment;
import it.sharengo.development.ui.slideshow.SlideshowFragment;
import it.sharengo.development.ui.splash.SplashFragment;
import it.sharengo.development.ui.tripend.TripEndFragment;
import it.sharengo.development.ui.tutorial.TutorialFragment;

/**
 * This component inject dependencies to all MvpFragments across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = { MvpFragmentModule.class })
public interface MvpFragmentComponent {

    void inject(SplashFragment fragment);
    void inject(HomeFragment fragment);
    void inject(MenuFragment fragment);

    void inject(MapFragment fragment);

    void inject(TripEndFragment fragment);

    void inject(LoginFragment fragment);

    void inject(ProfileFragment fragment);

    void inject(SlideshowFragment fragment);

    void inject(PasswordRecoveryFragment fragment);

    void inject(SignupFragment fragment);

    void inject(LongIntroFragment fragment);

    void inject(ShortIntroFragment fragment);

    void inject(SettingsFragment fragment);

    void inject(SettingsCitiesFragment fragment);

    void inject(SettingsLangFragment fragment);

    void inject(SettingsAddressesFragment fragment);

    void inject(SettingsAddressesNewFragment fragment);

    void inject(ChronologyFragment fragment);

    void inject(OnboardingFragment fragment);

    void inject(FeedsFragment fragment);

    void inject(FeedsDetailFragment fragment);

    void inject(AssistanceFragment fragment);

    void inject(ShareFragment fragment);

    void inject(MapGoogleFragment fragment);

    void inject(FaqFragment fragment);

    void inject(TutorialFragment fragment);
}
