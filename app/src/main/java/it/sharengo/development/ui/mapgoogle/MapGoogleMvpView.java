package it.sharengo.development.ui.mapgoogle;

import java.util.List;

import it.sharengo.development.data.models.Car;
import it.sharengo.development.data.models.City;
import it.sharengo.development.data.models.Feed;
import it.sharengo.development.data.models.Reservation;
import it.sharengo.development.data.models.SearchItem;
import it.sharengo.development.data.models.Trip;
import it.sharengo.development.ui.base.map.MvpMapView;
import it.sharengo.development.ui.base.presenters.MvpView;

public interface MapGoogleMvpView extends MvpMapView {

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
    void setNextCar(List<Car> carsList);
}
