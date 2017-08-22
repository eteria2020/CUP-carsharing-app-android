package it.sharengo.development.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.R;
import it.sharengo.development.data.datasources.CitiesDataSource;
import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.data.models.ResponseCity;
import okhttp3.Credentials;
import rx.Observable;
import rx.functions.Action1;

import static android.content.Context.MODE_PRIVATE;

@Singleton
public class AppRepository {

    public static final String TAG = AppRepository.class.getSimpleName();

    private boolean animateHome = true;
    private MenuItem.Section menuSelection;

    private CitiesDataSource mCitiesDataSource;
    private ResponseCity mChachedCities;

    private City mCityPreference;
    private String mLang;

    @Inject
    public AppRepository(CitiesDataSource citiesDataSource) {
        this.mCitiesDataSource = citiesDataSource;
    }

    public Observable<List<MenuItem>> getMenu() {

        List<MenuItem> menuItems = new ArrayList<>();

        menuItems.add(new MenuItem(MenuItem.Section.LOGIN));
        menuItems.add(new MenuItem(MenuItem.Section.SIGNUP));
        menuItems.add(new MenuItem(MenuItem.Section.FAQ));
        menuItems.add(new MenuItem(MenuItem.Section.RATES));
        menuItems.add(new MenuItem(MenuItem.Section.HELP));
        
        return Observable.just(menuItems);
    }

    public Observable<List<MenuItem>> getAuthMenu() {

        List<MenuItem> menuItems = new ArrayList<>();

        //menuItems.add(new MenuItem(MenuItem.Section.PROFILE));
        menuItems.add(new MenuItem(MenuItem.Section.BOOKING));
        menuItems.add(new MenuItem(MenuItem.Section.HISTORIC));
        menuItems.add(new MenuItem(MenuItem.Section.HELP));
        menuItems.add(new MenuItem(MenuItem.Section.FAQ));
        menuItems.add(new MenuItem(MenuItem.Section.BUY));
        //menuItems.add(new MenuItem(MenuItem.Section.SHARE));
        menuItems.add(new MenuItem(MenuItem.Section.SETTINGS));
        menuItems.add(new MenuItem(MenuItem.Section.LOGOUT));

        return Observable.just(menuItems);
    }

    public boolean animateHome(){
        return animateHome;
    }

    public void setAnimateHome(boolean animate){
        animateHome = animate;
    }

    public void selectMenuItem(MenuItem.Section selected){
        menuSelection = selected;
    }

    public MenuItem.Section getSelectMenuItem(){
        return menuSelection;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                              GET Cities
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Observable<ResponseCity> getCities(final Context context) {

        if(mChachedCities != null) {

            setFavoriteCity(context);
            return Observable.just(mChachedCities);

        }else{
            return mCitiesDataSource.getCities(Credentials.basic(context.getString(R.string.endpointCitiesUser), context.getString(R.string.endpointCitiesPass)))
                    .doOnNext(new Action1<ResponseCity>() {
                        @Override
                        public void call(ResponseCity response) {

                            createOrUpdateCitiesInMemory(context, response);
                        }
                    });
        }
    }

    private void createOrUpdateCitiesInMemory(Context context, ResponseCity response) {
        if (mChachedCities == null) {
            mChachedCities = new ResponseCity();
        }
        mChachedCities = response;

        setFavoriteCity(context);
    }

    private void setFavoriteCity(Context context){
        SharedPreferences mPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);

        for(City mCity : mChachedCities.data){
            if(mPref.getString(context.getString(R.string.preference_citiesfavourites),"").equals(mCity.id)){
                mCity.favourites = true;
                mCityPreference = mCity;
            }else{
                mCity.favourites = false;
            }
        }
    }

    public City getCityPreference(){
        return mCityPreference;
    }

    public void putLang(String lang){
        mLang = lang;
    }

    public String getLang(){
        return mLang;
    }
}
