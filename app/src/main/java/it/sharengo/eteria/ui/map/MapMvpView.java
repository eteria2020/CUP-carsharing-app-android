package it.sharengo.eteria.ui.map;

import java.util.List;

import it.sharengo.eteria.data.models.Car;
import it.sharengo.eteria.data.models.City;
import it.sharengo.eteria.data.models.Feed;
import it.sharengo.eteria.data.models.Reservation;
import it.sharengo.eteria.data.models.SearchItem;
import it.sharengo.eteria.data.models.Trip;
import it.sharengo.eteria.ui.base.presenters.MvpView;

public interface MapMvpView extends MvpView {

    void showCars(List<Car> carsList);
    void showFeeds(List<Feed> feedsList);
    void noCarsFound();
    void showSearchResult(List<SearchItem> searchItemList);
    void showBookingCar(Reservation reservation);
    void showConfirmDeletedCar();
    void showTripInfo(Car car, int timestamp_start);
    void setTripInfo(Trip trip);
    void removeTripInfo();
    void showReservationInfo(Car mCar, Reservation mReservation);
    void setReservationInfo(Car mCar, Reservation mReservation);
    void removeReservationInfo();
    void openTripEnd(int timestamp);
    void openNotification(int start, int end);
    void openReservationNotification();
    void openPreselectedCarPopup(Car car);
    void showCity(List<City> cityList);
    void setFeedInters();
}
