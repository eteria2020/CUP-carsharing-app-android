package it.sharengo.development.data.repositories;

import android.content.Context;

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

@Singleton
public class AppRepository {

    public static final String TAG = AppRepository.class.getSimpleName();

    private boolean animateHome = true;
    private MenuItem.Section menuSelection;

    private CitiesDataSource mCitiesDataSource;
    private List<City> mChachedCities;

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

        menuItems.add(new MenuItem(MenuItem.Section.PROFILE));
        menuItems.add(new MenuItem(MenuItem.Section.BOOKING));
        menuItems.add(new MenuItem(MenuItem.Section.HISTORIC));
        menuItems.add(new MenuItem(MenuItem.Section.HELP));
        menuItems.add(new MenuItem(MenuItem.Section.FAQ));
        menuItems.add(new MenuItem(MenuItem.Section.BUY));
        menuItems.add(new MenuItem(MenuItem.Section.SHARE));
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

    public Observable<ResponseCity> getCities(Context context) {

        return mCitiesDataSource.getCities(Credentials.basic(context.getString(R.string.endpointCitiesUser),context.getString(R.string.endpointCitiesPass)))
                .doOnNext(new Action1<ResponseCity>() {
                    @Override
                    public void call(ResponseCity response) {

                        createOrUpdateCitiesInMemory(response);
                    }
                });
    }

    private void createOrUpdateCitiesInMemory(ResponseCity response) {
        if (mChachedCities == null) {
            mChachedCities = new ArrayList<City>();
        }
        mChachedCities = response.data;

    }
}
