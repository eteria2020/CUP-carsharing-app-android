package it.sharengo.eteria.data.datasources.api;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.sharengo.eteria.injection.ApplicationContext;
import it.sharengo.eteria.utils.schedulers.SchedulerProvider;

/**
 * Provide data-level dependencies.
 */
public class MockApiModule extends ApiModule {

    @Override
    SampleApi provideSampleApi(@ApplicationContext Context context, 
                               SchedulerProvider schedulerProvider) {
        return new MockApi();
    }
}
