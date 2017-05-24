package it.sharengo.development.data.datasources.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.sharengo.development.BuildConfig;
import it.sharengo.development.R;
import it.sharengo.development.data.common.SerializationExclusionStrategy;
import it.sharengo.development.injection.ApplicationContext;
import it.sharengo.development.utils.schedulers.SchedulerProvider;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provide data-level dependencies.
 */
@Module
public class ApiModule {

    @Provides
    @Singleton
    SampleApi provideSampleApi(@ApplicationContext Context context, SchedulerProvider schedulerProvider) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new SerializationExclusionStrategy())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.endpoint))
                .client(httpClient.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(schedulerProvider.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(SampleApi.class);
    }
}
