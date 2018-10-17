package it.sharengo.eteria.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.eteria.R;
import it.sharengo.eteria.data.models.Car;
import it.sharengo.eteria.data.models.SearchItem;
import it.sharengo.eteria.injection.ApplicationContext;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class PreferencesRepository {

    public static final String TAG = PreferencesRepository.class.getSimpleName();

    static final String KEY_FIRST_ACCESS = "KEY_FIRST_ACCESS";
    static final String KEY_USERNAME = "KEY_USERNAME";
    static final String KEY_PASSWORD = "KEY_PASSWORD";
    static final String KEY_KML = "kml";
    //Reservation
    static final String KEY_RESERVATION_CAR = "KEY_RESERVATION_CAR";
    static final String KEY_RESERVATION_TIMESTAMP = "KEY_RESERVATION_TIMESTAMP";

    private final Context mContext;

    private SharedPreferences mReservationPreferences;
    private SharedPreferences mPref;
    private List<SearchItem> mSearchResults;
    private List<SearchItem> mFavourites;

    @Inject
    public PreferencesRepository(@ApplicationContext Context context) {
        this.mContext = context;
    }

    /**
     * Clear shared preferences of app.
     *
     * @param  prefs  shared preference of app
     */
    public void clear(SharedPreferences prefs) {
        prefs.edit().clear().apply();
    }

    /*
     *
     *      PRIVATE
     *
     */

    private boolean setBoolean(String key, boolean data) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, data);
        return editor.commit();
    }

    private boolean setInt(String key, int data) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(key, data);
        return editor.commit();
    }

    private boolean setString(String key, String data) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, data);
        return editor.commit();
    }

    private boolean putString(String key, String data) {
        SharedPreferences.Editor editor = getmReservationPreferences().edit();
        editor.putString(key, data);
        return editor.commit();
    }

    private boolean putLong(String key, long data) {
        SharedPreferences.Editor editor = getmReservationPreferences().edit();
        editor.putLong(key, data);
        return editor.commit();
    }

    private boolean unset(String key) {
        SharedPreferences.Editor editor = getmReservationPreferences().edit();
        editor.remove(key);
        return editor.commit();
    }

    private SharedPreferences getmReservationPreferences() {
        if(mReservationPreferences==null)
            mReservationPreferences = mContext.getSharedPreferences(mContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return mReservationPreferences;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Auth
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Retrieve status of variable "firstAccess" to app.
     *
     * @param  prefs  shared preference of app
     * @return        status of variable "firstAccess" to app
     * @see           Boolean
     */
    public boolean getFirstAccess(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_FIRST_ACCESS, true);
    }

    /**
     * Save value for variable "firstAccess" to app.
     *
     * @param  firstAccess  value to set
     * @param  prefs        shared preference of app
     * @return              status of variable after operation
     * @see                 boolean
     */
    public boolean saveFirstAccess(boolean firstAccess, SharedPreferences prefs) {
        mPref = prefs;
        return setBoolean(KEY_FIRST_ACCESS, firstAccess);
    }

    /**
     * Save credentials to preference.
     *
     * @param  username  username of user
     * @param  password  password of user
     * @param  prefs     shared preference of app
     */
    public void saveCredentials(String username, String password, SharedPreferences prefs) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }

    /**
     * Retrieve username of user.
     *
     * @param  prefs        shared preference of app
     * @return              username of user
     * @see                 String
     */
    public String getUsername(SharedPreferences prefs){
        return prefs.getString(KEY_USERNAME, "");
    }

    /**
     * Retrieve password of user.
     *
     * @param  prefs        shared preference of app
     * @return              password of user
     * @see                 String
     */
    public String getPassword(SharedPreferences prefs){
        return prefs.getString(KEY_PASSWORD, "");
    }


    public void resetKml(SharedPreferences prefs){

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_KML, "");
        editor.commit();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Storico ricerche
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<List<SearchItem>> getHistoricSearch(final String searchText, SharedPreferences mPrefs, final String exclude) {

        mSearchResults = getHistoricList(mPrefs);

        Collections.sort(mSearchResults, new Comparator<SearchItem>() {

            @Override
            public int compare(SearchItem lhs,
                               SearchItem rhs) {
                // Do your comparison logic here and retrn accordingly.
                return rhs.type.compareTo(lhs.type);
            }
        });

        return Observable.from(mSearchResults)
                .filter(new Func1<SearchItem, Boolean>() {
                    @Override
                    public Boolean call(SearchItem item) {
                        if(exclude != null){
                            if(item.type.equals(exclude)) return false;
                        }
                        if(searchText.length() > 0)
                            return item.display_name.toLowerCase().equals(searchText.toLowerCase()); //filtering
                        else return true;
                    }
                })
                .toList();
    }

    public Observable<List<SearchItem>> getFavourite(Context context) {

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

    public void saveSearchResultOnFavourites(SharedPreferences mPrefs, SearchItem searchItem){
        List<SearchItem> results = getHistoricList(mPrefs);

        results.add(searchItem);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Type fooType = new TypeToken<List<SearchItem>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(results, fooType);
        prefsEditor.putString("SearchHistoric", json);
        prefsEditor.commit();
    }

    public void editSearchResultOnFavourites(SharedPreferences mPrefs, SearchItem searchItem, String name, String address){
        List<SearchItem> results = getHistoricList(mPrefs);

        for(SearchItem rI : results){
            if(rI.display_name != null && rI.name != null && rI.display_name.equals(searchItem.display_name) && rI.name.equals(searchItem.name)){
                rI.name = name;
                rI.display_name = address;
            }
        }

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Type fooType = new TypeToken<List<SearchItem>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(results, fooType);
        prefsEditor.putString("SearchHistoric", json);
        prefsEditor.commit();
    }

    public void addSearchResultOnFavourites(SharedPreferences mPrefs, SearchItem searchItem, String name, String address){
        List<SearchItem> results = getHistoricList(mPrefs);

        for(SearchItem rI : results){
            if(rI.name == null && rI.display_name.equals(searchItem.display_name)){
                rI.name = name;
                rI.display_name = address;
                rI.type = "favorite";
            }
        }

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Type fooType = new TypeToken<List<SearchItem>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(results, fooType);
        prefsEditor.putString("SearchHistoric", json);
        prefsEditor.commit();
    }

    public void deleteSearchResultOnFavourites(SharedPreferences mPrefs, SearchItem searchItem){
        List<SearchItem> results = getHistoricList(mPrefs);

        int i = 0;
        int find = 0;
        for(SearchItem rI : results){
            if(rI.display_name.equals(searchItem.display_name)){
                find = i;
            }
            i++;
        }

        results.remove(find);

        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Type fooType = new TypeToken<List<SearchItem>>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(results, fooType);
        prefsEditor.putString("SearchHistoric", json);
        prefsEditor.commit();
    }

    private List<SearchItem> getHistoricList(SharedPreferences mPrefs){

        List<SearchItem> results = new ArrayList<SearchItem>();

        Type fooType = new TypeToken<List<SearchItem>>() {}.getType();

        Gson gson = new Gson();
        String json = mPrefs.getString("SearchHistoric", "");
        List<SearchItem> obj = (ArrayList<SearchItem>) gson.fromJson(json, fooType);

        if(obj != null) results = obj;

        return results;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              Reservation
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public @Nullable Car getReservationCar() {
        Car car = null;
        String carEncoded = getmReservationPreferences().getString(KEY_RESERVATION_CAR, "");
        try{
            car = new Gson().fromJson(carEncoded, Car.class);
            Log.d("BOMB", "decoded car from preferences" + car.toString());
        }catch (Exception e) {
            Log.e(TAG, "getReservationCar: Exception", e);
        }
        return car;
    }

    public boolean saveReservationCar(Car car) {
        boolean result = false;
        try{
            Log.i("BOMB", "save reservationCar: " + car.toString());
            result = putString(KEY_RESERVATION_CAR, new Gson().toJson(car));
        }catch (Exception e) {
            Log.e(TAG, "saveReservationCar: Exception", e);
        }
        return result;
    }

    public long getReservationTimestamp() {

        return getmReservationPreferences().getLong(KEY_RESERVATION_TIMESTAMP, 0);
    }

    public boolean saveReservationTimestamp(long timestamp) {
        boolean result = false;
        try{
            result = putLong(KEY_RESERVATION_TIMESTAMP, timestamp);
        }catch (Exception e) {
            Log.e(TAG, "saveReservationTimestamp: Exception", e);
        }
        return result;
    }


    public boolean cleanReservationData() {
        boolean result = false;
        try{
            result = unset(KEY_RESERVATION_CAR);
            result &= unset(KEY_RESERVATION_TIMESTAMP);
        }catch (Exception e) {
            Log.e(TAG, "saveReservationTimestamp: Exception", e);
        }
        return result;
    }


}
