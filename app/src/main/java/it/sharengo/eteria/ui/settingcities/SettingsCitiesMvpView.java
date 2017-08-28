package it.sharengo.eteria.ui.settingcities;

import java.util.List;

import it.sharengo.eteria.data.models.City;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface SettingsCitiesMvpView extends MvpView {

    void showList(List<City> citiesList);

}
