package it.sharengo.eteria.data.datasources.api;

import java.util.List;
import java.util.Map;


import it.sharengo.eteria.data.models.Post;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface JsonPlaceholderApi {
    
    
    @GET("posts")
    Observable<Result<List<Post>>> getPosts(
            @QueryMap Map<String, String> filters
    );


}
