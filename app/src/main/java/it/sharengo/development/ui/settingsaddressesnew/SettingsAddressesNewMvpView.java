package it.sharengo.development.ui.settingsaddressesnew;

import java.util.List;

import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface SettingsAddressesNewMvpView extends MvpView {

    void showSearchResult(List<SearchItem> searchItemList);

}
