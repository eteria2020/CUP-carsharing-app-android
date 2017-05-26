package it.sharengo.development.data.datasources;

import java.util.List;
import java.util.Map;

import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.Post;
import rx.Observable;

public interface SharengoDataSource {

    Observable<List<Car>> getCars(float latitude, float longitude, float radius);

}
