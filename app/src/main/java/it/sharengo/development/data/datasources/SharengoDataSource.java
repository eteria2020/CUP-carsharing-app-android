package it.sharengo.development.data.datasources;

import it.sharengo.development.data.models.Response;
import it.sharengo.development.data.models.ResponseUser;
import it.sharengo.development.data.models.User;
import rx.Observable;

public interface SharengoDataSource {

    Observable<Response> getCars(float latitude, float longitude, int radius);

    Observable<Response> getPlates();

    Observable<ResponseUser> getUser();

}
