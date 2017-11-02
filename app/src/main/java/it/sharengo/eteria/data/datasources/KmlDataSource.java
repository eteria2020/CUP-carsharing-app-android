package it.sharengo.eteria.data.datasources;

import it.sharengo.eteria.data.models.Kml;
import rx.Observable;

public interface KmlDataSource {

    Observable<Kml> zone();

}
