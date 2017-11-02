package it.sharengo.eteria.data.datasources;

import java.util.List;

import it.sharengo.eteria.data.models.Address;
import rx.Observable;

public interface SharengoMapDataSource {

    Observable<List<Address>> searchAddress(String address, String format);

}
