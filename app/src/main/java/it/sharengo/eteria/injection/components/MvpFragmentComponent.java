package it.sharengo.eteria.injection.components;

import dagger.Component;
import it.sharengo.eteria.injection.PerActivity;
import it.sharengo.eteria.injection.modules.MvpFragmentModule;
import it.sharengo.eteria.ui.buyminutes.BuyMinutesFragment;
import it.sharengo.eteria.ui.assistance.AssistanceFragment;
import it.sharengo.eteria.ui.chronology.ChronologyFragment;
import it.sharengo.eteria.ui.faq.FaqFragment;
import it.sharengo.eteria.ui.feeds.FeedsFragment;
import it.sharengo.eteria.ui.feedsdetail.FeedsDetailFragment;
import it.sharengo.eteria.ui.login.LoginFragment;
import it.sharengo.eteria.ui.longintro.LongIntroFragment;
import it.sharengo.eteria.ui.home.HomeFragment;
import it.sharengo.eteria.ui.mapgoogle.MapGoogleFragment;
import it.sharengo.eteria.ui.menu.MenuFragment;
import it.sharengo.eteria.ui.onboarding.OnboardingFragment;
import it.sharengo.eteria.ui.passwordrecovery.PasswordRecoveryFragment;
import it.sharengo.eteria.ui.profile.ProfileFragment;
import it.sharengo.eteria.ui.rates.RatesFragment;
import it.sharengo.eteria.ui.settingcities.SettingsCitiesFragment;
import it.sharengo.eteria.ui.settings.SettingsFragment;
import it.sharengo.eteria.ui.settingsaddresses.SettingsAddressesFragment;
import it.sharengo.eteria.ui.settingsaddressesnew.SettingsAddressesNewFragment;
import it.sharengo.eteria.ui.settingslang.SettingsLangFragment;
import it.sharengo.eteria.ui.share.ShareFragment;
import it.sharengo.eteria.ui.shortintro.ShortIntroFragment;
import it.sharengo.eteria.ui.signup.SignupFragment;
import it.sharengo.eteria.ui.slideshow.SlideshowFragment;
import it.sharengo.eteria.ui.splash.SplashFragment;
import it.sharengo.eteria.ui.tripend.TripEndFragment;
import it.sharengo.eteria.ui.tutorial.TutorialFragment;
import it.sharengo.eteria.ui.userarea.UserAreaFragment;

/**
 * This component inject dependencies to all MvpFragments across the application
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = { MvpFragmentModule.class })
public interface MvpFragmentComponent {

    void inject(SplashFragment fragment);
    void inject(HomeFragment fragment);
    void inject(MenuFragment fragment);

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

    void inject(BuyMinutesFragment fragment);

    void inject(UserAreaFragment fragment);

    void inject(RatesFragment fragment);
}
