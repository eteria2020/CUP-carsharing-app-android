package it.sharengo.eteria.data.datasources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.sharengo.eteria.data.datasources.api.JsonPlaceholderApi;
import it.sharengo.eteria.data.datasources.base.BaseRetrofitDataSource;
import it.sharengo.eteria.data.models.Post;
import rx.Observable;

public class JsonPlaceholderRetrofitDataSource extends BaseRetrofitDataSource implements JsonPlaceholderDataSource {

    private final JsonPlaceholderApi mJsonPlaceholderApi;


    public JsonPlaceholderRetrofitDataSource(JsonPlaceholderApi mSampleApi) {
        this.mJsonPlaceholderApi = mSampleApi;
    }

    @Override
    public Observable<List<Post>> getPosts() {
        return getPosts(new HashMap<String, String>());
    }

    @Override
    public Observable<List<Post>> getPosts(Map<String, String> filters) {
        return mJsonPlaceholderApi.getPosts(filters)
                .compose(this.<List<Post>>handleRetrofitRequest());
    }


}
