package it.sharengo.development.data.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class KmlServerPolygon implements Serializable {


    public List<LatLng> coordinates;

    public KmlServerPolygon() {
    }

    public KmlServerPolygon(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }
}
