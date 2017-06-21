package it.sharengo.development.injection.components;

import dagger.Component;
import it.sharengo.development.injection.PerActivity;
import it.sharengo.development.injection.modules.MvpFragmentModule;
import it.sharengo.development.ui.login.LoginFragment;
import it.sharengo.development.ui.longintro.LongIntroFragment;
import it.sharengo.development.ui.map.MapFragment;
import it.sharengo.development.ui.home.HomeFragment;
import it.sharengo.development.ui.menu.MenuFragment;
import it.sharengo.development.ui.passwordrecovery.PasswordRecoveryFragment;
import it.sharengo.development.ui.profile.ProfileFragment;
import it.sharengo.development.ui.shortintro.ShortIntroFragment;
import it.sharengo.development.ui.signup.SignupFragment;
import it.sharengo.development.ui.slideshow.SlideshowFragment;
import it.sharengo.development.ui.splash.SplashFragment;
import it.sharengo.development.ui.tripend.TripEndFragment;

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
}
