package it.sharengo.development.data.datasources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.sharengo.development.data.datasources.api.JsonPlaceholderApi;
import it.sharengo.development.injection.RemoteDataSource;

@Module
public class DataSourceModule {

    @Provides
    @Singleton
    @RemoteDataSource
    JsonPlaceholderDataSource provideRemoteDataSource(JsonPlaceholderApi api) {
        return new JsonPlaceholderRetrofitDataSource(api);
    }

    
}
