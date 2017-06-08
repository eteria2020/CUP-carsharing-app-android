package it.sharengo.development.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.R;
import it.sharengo.development.data.models.SearchItem;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class UserRepository {

    public static final String TAG = UserRepository.class.getSimpleName();


    @Inject
    public UserRepository() {

    }


    public Observable<List<SearchItem>> getFavourite(final String searchText, Context context) {

        List<SearchItem> searchItems = new ArrayList<SearchItem>();
        searchItems.add(new SearchItem(context.getString(R.string.search_favoriteempty_label), "none"));

        return Observable.from(searchItems)
                .toList();
    }


}
