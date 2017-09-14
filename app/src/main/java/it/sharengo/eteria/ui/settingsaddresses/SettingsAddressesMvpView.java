package it.sharengo.eteria.ui.settingsaddresses;

import java.util.List;

import it.sharengo.eteria.data.models.SearchItem;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface SettingsAddressesMvpView extends MvpView {

    void showEmptyResult();
    void showList(List<SearchItem> searchItems);

}
