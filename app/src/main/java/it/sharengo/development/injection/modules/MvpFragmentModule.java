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
import it.sharengo.development.ui.longintro.LongIntroPresenter;
import it.sharengo.development.ui.map.MapPresenter;
import it.sharengo.development.ui.base.presenters.PresenterManager;
import it.sharengo.development.ui.home.HomePresenter;
import it.sharengo.development.ui.menu.MenuPresenter;
import it.sharengo.development.ui.passwordrecovery.PasswordRecoveryPresenter;
import it.sharengo.development.ui.profile.ProfilePresenter;
import it.sharengo.development.ui.shortintro.ShortIntroPresenter;
import it.sharengo.development.ui.signup.SignupPresenter;
import it.sharengo.development.ui.slideshow.SlideshowPresenter;
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
                                       AppRepository appRepository,
                                       UserRepository userRepository) {
        HomePresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new HomePresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    MenuPresenter provideMenuPresenter(PresenterManager presenterManager, 
                                       SchedulerProvider schedulerProvider,
                                       AppRepository appRepository,
                                       UserRepository userRepository) {
        MenuPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new MenuPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    MapPresenter provideMapPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository,
                                     PostRepository postRepository, CarRepository carRepository,
                                     AddressRepository addressRepository, PreferencesRepository preferencesRepository,
                                     UserRepository userRepository) {
        MapPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new MapPresenter(schedulerProvider, appRepository, postRepository, carRepository, addressRepository, preferencesRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    TripEndPresenter provideTripEndPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository) {
        TripEndPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new TripEndPresenter(schedulerProvider, appRepository);
        }
        return presenter;
    }

    @Provides
    LoginPresenter provideLoginPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider,
                                         AppRepository appRepository,
                                         UserRepository userRepository,
                                         PreferencesRepository preferencesRepository) {
        LoginPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new LoginPresenter(schedulerProvider, appRepository, userRepository, preferencesRepository);
        }
        return presenter;
    }

    @Provides
    ProfilePresenter provideProfilePresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider,
                                             AppRepository appRepository) {
        ProfilePresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new ProfilePresenter(schedulerProvider, appRepository);
        }
        return presenter;
    }

    @Provides
    SlideshowPresenter provideSlideshowPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository) {
        SlideshowPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SlideshowPresenter(schedulerProvider, appRepository);
        }
        return presenter;
    }

    @Provides
    PasswordRecoveryPresenter providePasswordRecoveryPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository) {
        PasswordRecoveryPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new PasswordRecoveryPresenter(schedulerProvider, appRepository);
        }
        return presenter;
    }

    @Provides
    SignupPresenter provideSignupPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository) {
        SignupPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SignupPresenter(schedulerProvider, appRepository);
        }
        return presenter;
    }

    @Provides
    LongIntroPresenter provideLongIntroPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider) {
        LongIntroPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new LongIntroPresenter(schedulerProvider);
        }
        return presenter;
    }

    @Provides
    ShortIntroPresenter provideShortIntroPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider) {
        ShortIntroPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new ShortIntroPresenter(schedulerProvider);
        }
        return presenter;
    }
}
