package it.sharengo.eteria.data.repositories;

import android.os.Bundle;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.eteria.data.datasources.SharengoDataSource;
import it.sharengo.eteria.data.models.Config;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Fulvio on 07/05/2018.
 */
@Singleton
public class ConfigRepository extends BaseRepository<List<Config>>{
    public static final String TAG = ConfigRepository.class.getSimpleName();

    public static final String CALL_CENTER_NUMBER = "call_center_number";

    private Bundle config = new Bundle();

    private Subscription configSubscriber = null;


    @Inject
    public ConfigRepository(SharengoDataSource mRemoteDataSource) {
        this.mRemoteDataSource = mRemoteDataSource;
    }

    private Observable<List<Config>> getConfigRemote(){

        if(shouldUpdateData()) {
            return mRemoteDataSource.getConfig()
                    .concatMap(this::handleResponse);
        }else
            return Observable.just(cache);

    }
    public Observable<Config> getConfig(){
        return getConfigRemote()
                    .concatMap(configs -> {
                        publishSubject.onNext(configs);
                        return Observable.from(configs);
                    })
                    .doOnSubscribe(()->config=new Bundle())
                    .doOnNext(configs ->config.putString(configs.config_key, configs.config_value));
    }
    public void updateConfig(){
        if(configSubscriber==null || configSubscriber.isUnsubscribed()) {

            configSubscriber = getConfig()
                    .observeOn(Schedulers.io())
                    .subscribe(new Observer<Config>() {


                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {


                        }

                        @Override
                        public void onNext(Config configs) {

                        }
                    });
        }
    }

    public String getCachedCallCenterNumber(){

        return config.getString(CALL_CENTER_NUMBER,"+3905861975772");
    }

    @Override
    long getUdateTime() {
        return System.currentTimeMillis()/1000 - 10*60;
    }
}
