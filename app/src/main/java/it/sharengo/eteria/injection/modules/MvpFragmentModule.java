package it.sharengo.eteria.injection.modules;

import android.os.Bundle;

import dagger.Module;
import dagger.Provides;
import it.sharengo.eteria.data.repositories.AddressRepository;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.data.repositories.CarRepository;
import it.sharengo.eteria.data.repositories.CityRepository;
import it.sharengo.eteria.data.repositories.KmlRepository;
import it.sharengo.eteria.data.repositories.PostRepository;
import it.sharengo.eteria.data.repositories.PreferencesRepository;
import it.sharengo.eteria.data.repositories.UserRepository;
import it.sharengo.eteria.ui.buyminutes.BuyMinutesPresenter;
import it.sharengo.eteria.ui.assistance.AssistancePresenter;
import it.sharengo.eteria.ui.chronology.ChronologyPresenter;
import it.sharengo.eteria.ui.faq.FaqPresenter;
import it.sharengo.eteria.ui.feeds.FeedsPresenter;
import it.sharengo.eteria.ui.feedsdetail.FeedsDetailPresenter;
import it.sharengo.eteria.ui.login.LoginPresenter;
import it.sharengo.eteria.ui.longintro.LongIntroPresenter;
import it.sharengo.eteria.ui.map.MapPresenter;
import it.sharengo.eteria.ui.base.presenters.PresenterManager;
import it.sharengo.eteria.ui.home.HomePresenter;
import it.sharengo.eteria.ui.mapgoogle.MapGooglePresenter;
import it.sharengo.eteria.ui.menu.MenuPresenter;
import it.sharengo.eteria.ui.onboarding.OnboardingPresenter;
import it.sharengo.eteria.ui.passwordrecovery.PasswordRecoveryPresenter;
import it.sharengo.eteria.ui.profile.ProfilePresenter;
import it.sharengo.eteria.ui.rates.RatesPresenter;
import it.sharengo.eteria.ui.settingcities.SettingsCitiesPresenter;
import it.sharengo.eteria.ui.settings.SettingsPresenter;
import it.sharengo.eteria.ui.settingsaddresses.SettingsAddressesPresenter;
import it.sharengo.eteria.ui.settingsaddressesnew.SettingsAddressesNewPresenter;
import it.sharengo.eteria.ui.settingslang.SettingsLangPresenter;
import it.sharengo.eteria.ui.share.SharePresenter;
import it.sharengo.eteria.ui.shortintro.ShortIntroPresenter;
import it.sharengo.eteria.ui.signup.SignupPresenter;
import it.sharengo.eteria.ui.slideshow.SlideshowPresenter;
import it.sharengo.eteria.ui.splash.SplashPresenter;
import it.sharengo.eteria.ui.tripend.TripEndPresenter;
import it.sharengo.eteria.ui.tutorial.TutorialPresenter;
import it.sharengo.eteria.ui.userarea.UserAreaPresenter;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

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
    SplashPresenter provideSplashPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, PreferencesRepository preferencesRepository, UserRepository userRepository, AppRepository appRepository) {
        SplashPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SplashPresenter(schedulerProvider, preferencesRepository, userRepository, appRepository);
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
                                     UserRepository userRepository, CityRepository cityRepository) {
        MapPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new MapPresenter(schedulerProvider, appRepository, postRepository, carRepository, addressRepository, preferencesRepository, userRepository, cityRepository);
        }
        return presenter;
    }

    @Provides
    TripEndPresenter provideTripEndPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        TripEndPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new TripEndPresenter(schedulerProvider, appRepository, userRepository);
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
                                             AppRepository appRepository, UserRepository userRepository) {
        ProfilePresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new ProfilePresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    SlideshowPresenter provideSlideshowPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        SlideshowPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SlideshowPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    PasswordRecoveryPresenter providePasswordRecoveryPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        PasswordRecoveryPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new PasswordRecoveryPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    SignupPresenter provideSignupPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        SignupPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SignupPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    LongIntroPresenter provideLongIntroPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        LongIntroPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new LongIntroPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    ShortIntroPresenter provideShortIntroPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, UserRepository userRepository) {
        ShortIntroPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new ShortIntroPresenter(schedulerProvider, userRepository);
        }
        return presenter;
    }

    @Provides
    SettingsPresenter provideSettingsPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        SettingsPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SettingsPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    SettingsCitiesPresenter provideSettingsCitiesPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        SettingsCitiesPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SettingsCitiesPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    SettingsLangPresenter provideSettingsLangPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        SettingsLangPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SettingsLangPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    SettingsAddressesPresenter provideSettingsAddressesPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, PreferencesRepository preferencesRepository, UserRepository userRepository) {
        SettingsAddressesPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SettingsAddressesPresenter(schedulerProvider, appRepository, preferencesRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    SettingsAddressesNewPresenter provideSettingsAddressesNewPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, AddressRepository addressRepository, PreferencesRepository preferencesRepository, UserRepository userRepository) {
        SettingsAddressesNewPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SettingsAddressesNewPresenter(schedulerProvider, appRepository, addressRepository, preferencesRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    ChronologyPresenter provideChronologyPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        ChronologyPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new ChronologyPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    OnboardingPresenter provideOnboardingPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        OnboardingPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new OnboardingPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    FeedsPresenter provideFeedsPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, CityRepository cityRepository, UserRepository userRepository) {
        FeedsPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new FeedsPresenter(schedulerProvider, appRepository, cityRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    FeedsDetailPresenter provideFeedsDetailPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        FeedsDetailPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new FeedsDetailPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    AssistancePresenter provideAssistancePresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        AssistancePresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new AssistancePresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    SharePresenter providesharePresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        SharePresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SharePresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    MapGooglePresenter provideMapGooglePresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository,
                                                 PostRepository postRepository, CarRepository carRepository,
                                                 AddressRepository addressRepository, PreferencesRepository preferencesRepository,
                                                 UserRepository userRepository, CityRepository cityRepository,
                                                 KmlRepository kmlRepository) {
        MapGooglePresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new MapGooglePresenter(schedulerProvider, appRepository, postRepository, carRepository, addressRepository, preferencesRepository, userRepository, cityRepository, kmlRepository);
        }
        return presenter;
    }

    @Provides
    FaqPresenter provideFaqPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        FaqPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new FaqPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    TutorialPresenter provideTutorialPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        TutorialPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new TutorialPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    BuyMinutesPresenter provideBuyMinutesPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        BuyMinutesPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new BuyMinutesPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    UserAreaPresenter provideUserAreaPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        UserAreaPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new UserAreaPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }

    @Provides
    RatesPresenter provideRatesPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider, AppRepository appRepository, UserRepository userRepository) {
        RatesPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new RatesPresenter(schedulerProvider, appRepository, userRepository);
        }
        return presenter;
    }
}
