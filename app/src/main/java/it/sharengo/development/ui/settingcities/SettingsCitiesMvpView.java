package it.sharengo.development.ui.settingcities;

import java.util.List;

import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.MenuItem;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface SettingsCitiesMvpView extends MvpView {

    void showList(List<City> citiesList);

}
