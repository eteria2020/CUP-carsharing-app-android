package it.sharengo.development.data.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.development.data.models.MenuItem;
import rx.Observable;

@Singleton
public class AppRepository {

    public static final String TAG = AppRepository.class.getSimpleName();

    private boolean animateHome = true;

    @Inject
    public AppRepository() {
        
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
}
