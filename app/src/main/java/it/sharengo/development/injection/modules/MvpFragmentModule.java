package it.sharengo.development.injection.modules;

import android.content.SharedPreferences;
import android.os.Bundle;

import dagger.Module;
import dagger.Provides;
import it.sharengo.development.data.repositories.AddressRepository;
import it.sharengo.development.data.repositories.AppRepository;
import it.sharengo.development.data.repositories.CarRepository;
import it.sharengo.development.data.repositories.PostRepository;
import it.sharengo.development.data.repositories.PreferencesRepository;
import it.sharengo.development.data.repositories.UserRepository;
import it.sharengo.development.ui.login.LoginPresenter;
import it.sharengo.development.ui.map.MapPresenter;
import it.sharengo.development.ui.base.presenters.PresenterManager;
import it.sharengo.development.ui.home.HomePresenter;
import it.sharengo.development.ui.menu.MenuPresenter;
import it.sharengo.development.ui.splash.SplashPresenter;
import it.sharengo.development.ui.tripend.TripEndPresenter;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

@Module
public class MvpFragmentModule {

    public Bundle mBundle;

    public MvpFragmentModule(Bundle bundle) {
        mBundle = bundle;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //
    //          SPECIFICARE DI SEGUITO TUTTI I PROVIDERE DI TUTTI I PRESENTER DELL'APP
    //
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Provides
    SplashPresenter provideSplashPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, PreferencesRepository preferencesRepository, UserRepository userRepository) {
        SplashPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SplashPresenter(schedulerProvider, preferencesRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    HomePresenter provideHomePresenter(PresenterManager presenterManager,
                                       SchedulerProvider schedulerProvider,
                                       //AppRepository appRepository,
                                       UserRepository userRepository) {
        HomePresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new HomePresenter(schedulerProvider, userRepository);
        }
        return presenter;
    }

    @Provides
    MenuPresenter provideMenuPresenter(PresenterManager presenterManager, 
                                       SchedulerProvider schedulerProvider,
                                       AppRepository appRepository) {
        MenuPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new MenuPresenter(schedulerProvider, appRepository);
        }
        return presenter;
    }

    @Provides
    MapPresenter provideMapPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider,
                                     PostRepository postRepository, CarRepository carRepository,
                                     AddressRepository addressRepository, PreferencesRepository preferencesRepository,
                                     UserRepository userRepository) {
        MapPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new MapPresenter(schedulerProvider, postRepository, carRepository, addressRepository, preferencesRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    TripEndPresenter provideTripEndPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider) {
        TripEndPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new TripEndPresenter(schedulerProvider);
        }
        return presenter;
    }

    @Provides
    LoginPresenter provideLoginPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider,
                                         UserRepository userRepository,
                                         PreferencesRepository preferencesRepository) {
        LoginPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new LoginPresenter(schedulerProvider, userRepository, preferencesRepository);
        }
        return presenter;
    }
}
