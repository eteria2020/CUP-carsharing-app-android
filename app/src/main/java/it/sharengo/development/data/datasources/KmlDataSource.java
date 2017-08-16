package it.sharengo.development.data.datasources;

import java.util.List;

import it.sharengo.development.data.models.Address;
import it.sharengo.development.data.models.Kml;
import rx.Observable;

public interface KmlDataSource {

    Observable<Kml> zone();

}
