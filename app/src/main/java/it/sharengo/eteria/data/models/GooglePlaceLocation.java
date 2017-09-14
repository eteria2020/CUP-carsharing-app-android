package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

public class GooglePlaceLocation {

    @SerializedName("lat")
    public float latitude;

    @SerializedName("lng")
    public float longitude;

    public GooglePlaceLocation() {
    }

    private GooglePlaceLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}


/*
*
         "geometry" : {
            "location" : {
               "lat" : 45.4422852,
               "lng" : 9.095534400000002
            }
*
* */