package it.sharengo.eteria.data.datasources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.sharengo.eteria.data.datasources.api.CitiesApi;
import it.sharengo.eteria.data.datasources.api.JsonPlaceholderApi;
import it.sharengo.eteria.data.datasources.api.KmlApi;
import it.sharengo.eteria.data.datasources.api.SharengoApi;
import it.sharengo.eteria.data.datasources.api.SharengoMapApi;

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

    @Provides
    @Singleton
    CitiesDataSource provideCitiesRemoteDataSource(CitiesApi api) {
        return new CitiesRetrofitDataSource(api);
    }

    @Provides
    @Singleton
    KmlDataSource provideKmlRemoteDataSource(KmlApi api) {
        return new KmlRetrofitDataSource(api);
    }
    
}
