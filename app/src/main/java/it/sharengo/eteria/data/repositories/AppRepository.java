package it.sharengo.eteria.data.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.sharengo.eteria.data.models.MenuItem;
import rx.Observable;

@Singleton
public class AppRepository {

    public static final String TAG = AppRepository.class.getSimpleName();

    @Inject
    public AppRepository() {
        
    }

    public Observable<List<MenuItem>> getMenu() {

        List<MenuItem> menuItems = new ArrayList<>();

        menuItems.add(new MenuItem(MenuItem.Section.HOME));
        
        return Observable.just(menuItems);
    }
}
