package it.sharengo.development.ui.base.map;


import it.sharengo.development.ui.base.presenters.MvpView;

/**
 * Base interface that any class that wants to act as a View in the MVP (Model View Presenter)
 * pattern must implement. Generally this interface will be extended by a more specific interface
 * that then usually will be implemented by an Activity or Fragment.
 */
public interface MvpMapView extends MvpView {
    void showEmptyMap();
    void moveMapCameraToDefaultLocation();
    void moveMapCameraTo(Double latitude, Double longitude);
    void animateMapCamera(Double latitude, Double longitude);
    void animateMapCamera(Double latitude, Double longitude, float zoom);

}