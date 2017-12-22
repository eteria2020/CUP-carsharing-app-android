package it.sharengo.eteria.data.datasources;

import java.util.List;

import it.sharengo.eteria.data.models.Address;
import it.sharengo.eteria.data.models.ResponseSharengoMap;
import rx.Observable;

public interface SharengoMapDataSource {

    Observable<ResponseSharengoMap> searchAddress(String address, String format);

}
