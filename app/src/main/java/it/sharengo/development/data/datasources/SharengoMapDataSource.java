package it.sharengo.development.data.datasources;

import java.util.List;

import it.sharengo.development.data.models.Address;
import it.sharengo.development.data.models.Cars;
import rx.Observable;

public interface SharengoMapDataSource {

    Observable<List<Address>> searchAddress(String address, String format);

}
