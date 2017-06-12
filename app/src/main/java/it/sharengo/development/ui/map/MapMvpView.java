package it.sharengo.development.ui.map;

import java.util.List;

import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.Reservation;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface MapMvpView extends MvpView {

    void showCars(List<Car> carsList);
    void noCarsFound();
    void showSearchResult(List<SearchItem> searchItemList);
    void showBookingCar(Reservation reservation);
    void showConfirmDeletedCar();
    void showTripInfo(Car car);
    void setTripInfo(Trip trip);
    void removeTripInfo();
    void showReservationInfo(Car mCar, Reservation mReservation);
    void setReservationInfo(Car mCar, Reservation mReservation);
    void removeReservationInfo();
    void openTripEnd(int timestamp);
    void openNotification(int start, int end);
}
