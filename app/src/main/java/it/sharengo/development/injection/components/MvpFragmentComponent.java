package it.sharengo.development.injection.components;

import dagger.Component;
import it.sharengo.development.injection.PerActivity;
import it.sharengo.development.injection.modules.MvpFragmentModule;
import it.sharengo.development.ui.map.MapFragment;
import it.sharengo.development.ui.home.HomeFragment;
import it.sharengo.development.ui.menu.MenuFragment;
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
}
