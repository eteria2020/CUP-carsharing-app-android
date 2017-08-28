package it.sharengo.eteria.data.datasources;

import java.util.List;
import java.util.Map;

import it.sharengo.eteria.data.models.Post;
import rx.Observable;

public interface JsonPlaceholderDataSource {

    Observable<List<Post>> getPosts();

    Observable<List<Post>> getPosts(Map<String, String> filters);
    
}
