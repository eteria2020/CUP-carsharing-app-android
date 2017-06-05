package it.sharengo.development.data.datasources;

import it.sharengo.development.data.models.Cars;
import rx.Observable;

public interface SharengoDataSource {

    Observable<Cars> getCars(float latitude, float longitude, int radius);

    Observable<Cars> getPlates();

}
