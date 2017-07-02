package it.sharengo.development.ui.settingsaddresses;

import java.util.List;

import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface SettingsAddressesMvpView extends MvpView {

    void showEmptyResult();
    void showList(List<SearchItem> searchItems);

}
