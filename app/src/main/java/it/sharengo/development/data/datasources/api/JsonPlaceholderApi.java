package it.sharengo.development.data.datasources.api;

import java.util.List;
import java.util.Map;


import it.sharengo.development.data.models.Post;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface JsonPlaceholderApi {
    
    @GET("posts")
    Observable<Result<List<Post>>> getPosts(
            @QueryMap Map<String, String> filters
    );

}
