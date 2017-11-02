package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

public class GoogleOverviewPolyline {

    public String points;

    public GoogleOverviewPolyline() {
    }

    private GoogleOverviewPolyline(String points) {
        this.points = points;
    }
}