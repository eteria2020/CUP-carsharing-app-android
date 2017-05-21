package it.sharengo.eteria.injection.modules;

import android.os.Bundle;

import dagger.Module;
import dagger.Provides;
import it.sharengo.eteria.data.repositories.AppRepository;
import it.sharengo.eteria.ui.map.MapPresenter;
import it.sharengo.eteria.ui.base.presenters.PresenterManager;
import it.sharengo.eteria.ui.home.HomePresenter;
import it.sharengo.eteria.ui.menu.MenuPresenter;
import it.sharengo.eteria.ui.splash.SplashPresenter;
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
    SplashPresenter provideSplashPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider) {
        SplashPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new SplashPresenter(schedulerProvider);
        }
        return presenter;
    }

    @Provides
    HomePresenter provideHomePresenter(PresenterManager presenterManager,
                                       SchedulerProvider schedulerProvider,
                                       AppRepository appRepository) {
        HomePresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new HomePresenter(schedulerProvider);
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
    MapPresenter provideMapPresenter(PresenterManager presenterManager, SchedulerProvider schedulerProvider) {
        MapPresenter presenter = null;
        if (mBundle != null) {
            presenter = presenterManager.restorePresenter(mBundle);
        }
        if (presenter == null) {
            presenter = new MapPresenter(schedulerProvider);
        }
        return presenter;
    }
}
