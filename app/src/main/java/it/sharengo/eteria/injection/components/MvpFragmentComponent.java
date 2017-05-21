package it.sharengo.eteria.injection.components;

import dagger.Component;
import it.sharengo.eteria.injection.PerActivity;
import it.sharengo.eteria.injection.modules.MvpFragmentModule;
import it.sharengo.eteria.ui.map.MapFragment;
import it.sharengo.eteria.ui.home.HomeFragment;
import it.sharengo.eteria.ui.menu.MenuFragment;
import it.sharengo.eteria.ui.splash.SplashFragment;

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
}
