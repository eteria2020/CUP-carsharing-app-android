package it.sharengo.development.data.datasources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.sharengo.development.data.datasources.api.JsonPlaceholderApi;
import it.sharengo.development.data.datasources.api.SharengoApi;
import it.sharengo.development.data.datasources.api.SharengoMapApi;

@Module
public class DataSourceModule {

    @Provides
    @Singleton
    JsonPlaceholderDataSource provideRemoteDataSource(JsonPlaceholderApi api) {
        return new JsonPlaceholderRetrofitDataSource(api);
    }

    @Provides
    @Singleton
    SharengoDataSource provideSharengoRemoteDataSource(SharengoApi api) {
        return new SharengoRetrofitDataSource(api);
    }

    @Provides
    @Singleton
    SharengoMapDataSource provideSharengoMapRemoteDataSource(SharengoMapApi api) {
        return new SharengoMapRetrofitDataSource(api);
    }
    
}
