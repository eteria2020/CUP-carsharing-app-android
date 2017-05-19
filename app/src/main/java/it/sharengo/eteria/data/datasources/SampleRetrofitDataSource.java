package it.sharengo.eteria.data.datasources;

import javax.inject.Inject;

import it.sharengo.eteria.data.datasources.api.SampleApi;
import it.sharengo.eteria.data.datasources.base.BaseRetrofitDataSource;

public class SampleRetrofitDataSource extends BaseRetrofitDataSource {

    private final SampleApi mSampleApi;

    @Inject
    public SampleRetrofitDataSource(SampleApi mSampleApi) {
        this.mSampleApi = mSampleApi;
    }

//    @Override
//    public Observable<List<User>> getUsers() {
//        return getUsers(new HashMap<String, String>());
//    }
//
//    @Override
//    public Observable<List<User>> getUsers(Map<String, String> filters) {
//        return mSampleApi.getUsers(filters).compose(this.<List<User>>handleRetrofitRequest());
//    }
//
//    @Override
//    public Observable<User> getUser(int id) {
//        return mSampleApi.getUser(id).compose(this.<User>handleRetrofitRequest());
//    }
//
//    @Override
//    public Observable<User> createUser(User user) {
//        return mSampleApi.createUser(user).compose(this.<User>handleRetrofitRequest());
//    }
//
//    @Override
//    public Observable<User> updateUser(User user) {
//        return mSampleApi.updateUser(user.id, user).compose(this.<User>handleRetrofitRequest());
//    }
//
//    @Override
//    public Observable<User> deleteUser(int id) {
//        return mSampleApi.deleteUser(id).compose(this.<User>handleRetrofitRequest());
//    }

}
