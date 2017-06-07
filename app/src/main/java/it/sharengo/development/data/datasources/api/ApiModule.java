package it.sharengo.development.data.datasources.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

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
    JsonPlaceholderApi provideSampleApi(@ApplicationContext Context context, SchedulerProvider schedulerProvider) {
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

        return retrofit.create(JsonPlaceholderApi.class);
    }

    @Provides
    @Singleton
    SharengoMapApi provideSharengoMapApi(@ApplicationContext Context context, SchedulerProvider schedulerProvider) {
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
                .baseUrl(context.getString(R.string.endpointSharengoMaps))
                .client(httpClient.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(schedulerProvider.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(SharengoMapApi.class);
    }

    @Provides
    @Singleton
    SharengoApi provideSharengoApi(@ApplicationContext Context context, SchedulerProvider schedulerProvider) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        /*OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);*/

        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new SerializationExclusionStrategy())
                .create();

        Log.w("gson",": "+gson);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.endpointSharengo))
                //.baseUrl("http:gr3dcomunication.com/sharengo/")
                .client(provideOkHttpClientTrusted(context))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(schedulerProvider.io()))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(SharengoApi.class);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClientTrusted(Context context){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        try {

            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                        }
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };




            CertificateFactory cf = CertificateFactory.getInstance("X509");
            Certificate caServer;
            InputStream cert = context.getResources().openRawResource(R.raw.client);
            InputStream cert2 = context.getResources().openRawResource(R.raw.ca);
            try {
                // Key
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                keyStore.load(cert, context.getString(R.string.mystore_password).toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
                kmf.init(keyStore, context.getString(R.string.mystore_password).toCharArray());
                KeyManager[] keyManagers = kmf.getKeyManagers();

                // Trust
                caServer = cf.generateCertificate(cert2);
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                trustStore.setCertificateEntry("ca", caServer);
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                tmf.init(trustStore);
                TrustManager[] trustManagers = tmf.getTrustManagers();
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(keyManagers, trustManagers, null);

                httpClient.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)trustAllCerts[0]);
            } finally {
                cert.close();
            }
        }catch (Exception e){

        }

        httpClient.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            }
        );
        httpClient.readTimeout(20, TimeUnit.SECONDS);
        httpClient.connectTimeout(20, TimeUnit.SECONDS);

        return httpClient.build();
    }
}
