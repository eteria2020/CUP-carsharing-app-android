package it.handroix.location;

import android.location.Location;

/**
 * Created by luca on 25/07/14.
 */
public interface HdxLocationUpdateListener {

    public void onNewLocation(Location location);

    public void onLocationUnavailable();

    public void onRequestRationalFineLocationPermission();

    public void onFineLocationDenied();
}
