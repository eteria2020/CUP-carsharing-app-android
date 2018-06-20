package it.sharengo.eteria.data.datasources.api;

import java.util.List;

import it.sharengo.eteria.data.models.OsmPlace;
import it.sharengo.eteria.data.models.ResponseGooglePlace;
import it.sharengo.eteria.data.models.ResponseGoogleRoutes;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface OsmApi {


    @GET("search")
    Observable<Result<List<OsmPlace>>> searchPlaceOsm(@Query("q") String query, @Query("format") String format, @Query("polygon") String polygon, @Query("addressdetails") String addressdetails);


}
