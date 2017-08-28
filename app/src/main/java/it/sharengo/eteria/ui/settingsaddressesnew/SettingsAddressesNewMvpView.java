package it.sharengo.eteria.ui.settingsaddressesnew;

import java.util.List;

import it.sharengo.eteria.data.models.SearchItem;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface SettingsAddressesNewMvpView extends MvpView {

    void showSearchResult(List<SearchItem> searchItemList);

}
