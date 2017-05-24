package it.sharengo.development.injection.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import it.sharengo.development.App;
import it.sharengo.development.injection.ApplicationContext;
import it.sharengo.development.utils.schedulers.AndroidSchedulerProvider;
import it.sharengo.development.utils.schedulers.SchedulerProvider;

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
