package it.sharengo.eteria.ui.menu;

import java.util.List;

import it.sharengo.eteria.data.models.MenuItem;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface MenuMvpView extends MvpView {

     void showList(List<MenuItem> menuItemList);
     void logoutUser();
}
