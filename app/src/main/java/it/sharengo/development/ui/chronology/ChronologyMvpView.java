package it.sharengo.development.ui.chronology;

import java.util.List;

import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface ChronologyMvpView extends MvpView {

    void showList(List<Trip> tripList);
    void showEmptyResult();

}
