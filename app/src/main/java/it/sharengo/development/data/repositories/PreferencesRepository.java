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
import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.SearchItem;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class PreferencesRepository {

    public static final String TAG = PreferencesRepository.class.getSimpleName();


    private List<SearchItem> mSearchResults;
    private List<SearchItem> mFavourites;

    @Inject
    public PreferencesRepository() {

    }


    /*public Observable<List<SearchItem>> getSearchItems(String searchText, Context context, SharedPreferences mPrefs) {

        return Observable
                .concat(
                        getHistoricSearch(searchText, mPrefs),
                        getFavourite(searchText, context)
                );
    }*/

    public Observable<List<SearchItem>> getHistoricSearch(final String searchText, SharedPreferences mPrefs) {

        mSearchResults = getHistoricList(mPrefs);

        return Observable.from(mSearchResults)
                .filter(new Func1<SearchItem, Boolean>() {
                    @Override
                    public Boolean call(SearchItem item) {
                        if(searchText.length() > 0)
                            return item.display_name.toLowerCase().equals(searchText.toLowerCase()); //filtering
                        else return true;
                    }
                })
                .toList();
    }

    public Observable<List<SearchItem>> getFavourite(final String searchText, Context context) {

        List<SearchItem> searchItems = new ArrayList<SearchItem>();
        searchItems.add(new SearchItem(context.getString(R.string.search_favoriteempty_label), "none"));

        return Observable.from(searchItems)
                .toList();
    }


    public void saveSearchResultOnHistoric(SharedPreferences mPrefs, SearchItem searchItem){
        List<SearchItem> results = getHistoricList(mPrefs);

        boolean find = false;
        for(SearchItem sI : results){
            if(sI.display_name.equals(searchItem.display_name)) find = true;
        }

        if(!find) {
            searchItem.type = "historic";
            results.add(searchItem);

            SharedPreferences.Editor prefsEditor = mPrefs.edit();

            Type fooType = new TypeToken<List<SearchItem>>() {}.getType();
            Gson gson = new Gson();
            String json = gson.toJson(results, fooType);
            prefsEditor.putString("SearchHistoric", json);
            prefsEditor.commit();
        }
    }

    private List<SearchItem> getHistoricList(SharedPreferences mPrefs){

        List<SearchItem> results = new ArrayList<SearchItem>();

        Type fooType = new TypeToken<List<SearchItem>>() {}.getType();

        Gson gson = new Gson();
        String json = mPrefs.getString("SearchHistoric", "");
        List<SearchItem> obj = (ArrayList<SearchItem>) gson.fromJson(json, fooType);

        if(obj != null) results = obj;

        Log.w("results",": "+results);
        return results;
    }

}
