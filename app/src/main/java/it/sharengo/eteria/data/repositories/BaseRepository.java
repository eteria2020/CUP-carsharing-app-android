package it.sharengo.eteria.data.repositories;



import it.sharengo.eteria.data.datasources.SharengoDataSource;
import it.sharengo.eteria.data.models.ResponseReservation;
import it.sharengo.eteria.data.models.SharengoResponse;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Fulvio on 07/05/2018.
 */

public abstract class BaseRepository<T> {

    protected T cache =null;

    public PublishSubject<T> publishSubject = PublishSubject.create();

    protected long lastUpdate =0;

    protected SharengoDataSource mRemoteDataSource;

    private void createOrUpdateInMemory(T response) {
        cache = response;
    }

    protected boolean shouldUpdateData(){
        return cache == null || lastUpdate < getUdateTime();
    }

    protected Observable<T> handleResponse(SharengoResponse<T> response){
        createOrUpdateInMemory(response.data);
        lastUpdate = response.timestamp;
        return Observable.just(response.data);
    }

    abstract long getUdateTime();
}
