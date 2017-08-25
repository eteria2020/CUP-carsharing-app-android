package it.sharengo.development.data.datasources;

import it.sharengo.development.data.models.Kml;
import rx.Observable;

public interface KmlDataSource {

    Observable<Kml> zone();

}
