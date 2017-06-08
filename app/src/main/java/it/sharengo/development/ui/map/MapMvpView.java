package it.sharengo.development.ui.map;

import java.util.List;

import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface MapMvpView extends MvpView {

    void showCars(List<Car> carsList);
    void noCarsFound();
    void showSearchResult(List<SearchItem> searchItemList);
    void showBookingCar();
    void showConfirmDeletedCar();
}
