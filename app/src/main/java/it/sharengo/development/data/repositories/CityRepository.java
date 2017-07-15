package it.sharengo.development.data.repositories;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.R;
import it.sharengo.development.data.datasources.CitiesDataSource;
import it.sharengo.development.data.datasources.JsonPlaceholderDataSource;
import it.sharengo.development.data.models.FeedCategory;
import it.sharengo.development.data.models.Post;
import it.sharengo.development.data.models.ResponseCity;
import it.sharengo.development.data.models.ResponseFeedCategory;
import okhttp3.Credentials;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

@Singleton
public class CityRepository {

    public static final String TAG = CityRepository.class.getSimpleName();

    private CitiesDataSource mRemoteDataSource;
    private ResponseFeedCategory mChachedCategories;


    @Inject
    public CityRepository(CitiesDataSource remoteDataSource) {
        this.mRemoteDataSource = remoteDataSource;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Feed Categories
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<ResponseFeedCategory> getCategories(final Context context) {

        return mRemoteDataSource.getCategories(Credentials.basic(context.getString(R.string.endpointCitiesUser), context.getString(R.string.endpointCitiesPass)))
                .doOnNext(new Action1<ResponseFeedCategory>() {
                    @Override
                    public void call(ResponseFeedCategory response) {

                        createOrUpdateCategoriesInMemory(context, response);
                    }
                });
    }

    private void createOrUpdateCategoriesInMemory(Context context, ResponseFeedCategory response) {
        if (mChachedCategories == null) {
            mChachedCategories = new ResponseFeedCategory();
        }
        mChachedCategories = response;
    }
}
