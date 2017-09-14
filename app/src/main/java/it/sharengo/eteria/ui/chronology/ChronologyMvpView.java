package it.sharengo.eteria.ui.chronology;

import java.util.List;

import it.sharengo.eteria.data.models.Trip;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface ChronologyMvpView extends MvpView {

    void showList(List<Trip> tripList, float discount_rate);
    void showEmptyResult();
    void showChronError(Throwable e);

}
