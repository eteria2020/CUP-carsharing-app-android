package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

public class GooglePlaceGeometry {

    public GooglePlaceLocation location;

    public GooglePlaceGeometry() {
    }

    private GooglePlaceGeometry(GooglePlaceLocation location) {
        this.location = location;
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