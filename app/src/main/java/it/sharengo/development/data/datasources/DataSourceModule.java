package it.sharengo.development.data.datasources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.sharengo.development.data.datasources.api.JsonPlaceholderApi;

@Module
public class DataSourceModule {

    @Provides
    @Singleton
    JsonPlaceholderDataSource provideRemoteDataSource(JsonPlaceholderApi api) {
        return new JsonPlaceholderRetrofitDataSource(api);
    }

    
}
