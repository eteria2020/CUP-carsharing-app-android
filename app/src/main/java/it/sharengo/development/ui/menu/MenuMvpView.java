package it.sharengo.development.ui.menu;

import java.util.List;

import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface MenuMvpView extends MvpView {

     void showList(List<MenuItem> menuItemList);
}
