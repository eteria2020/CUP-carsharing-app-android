package it.sharengo.eteria.injection.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import it.sharengo.eteria.App;
import it.sharengo.eteria.injection.ApplicationContext;
import it.sharengo.eteria.utils.schedulers.AndroidSchedulerProvider;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

/**
 * Provide application-level dependencies.
 */
@Module
public class ApplicationModule {

    private final App mApplication;

    public ApplicationModule(App application) {
        mApplication = application;
    }

    @Provides
    App provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return AndroidSchedulerProvider.getInstance();
    }
    
}
