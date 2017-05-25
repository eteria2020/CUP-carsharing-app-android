package it.sharengo.development.data.datasources;

import java.util.List;

import it.sharengo.development.data.models.Post;
import rx.Observable;

/**
 * Created by gretaiaconisi on 25/05/17.
 */

public interface JsonPlaceholderDataSource {

    Observable<List<Post>> getPosts();
}
