package it.sharengo.development.data.datasources;

import java.util.HashMap;
import java.util.List;

import it.sharengo.development.data.datasources.api.JsonPlaceholderApi;
import it.sharengo.development.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.development.data.models.Post;
import rx.Observable;

/**
 * Created by gretaiaconisi on 25/05/17.
 */

public class JsonPlaceholderRetrofitDataSource extends BaseRetrofitDataSource implements JsonPlaceholderDataSource {

    private final JsonPlaceholderApi mSampleApi;

    JsonPlaceholderRetrofitDataSource(JsonPlaceholderApi mSampleApi) {
        this.mSampleApi = mSampleApi;
    }

    @Override
    public Observable<List<Post>> getPosts() {
        return mSampleApi.getPosts(new HashMap<String, String>())
                .compose(this.<List<Post>>handleRetrofitRequest());
    }
}
