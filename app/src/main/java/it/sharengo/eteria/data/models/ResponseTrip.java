package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import it.sharengo.eteria.data.common.ExcludeSerialization;

public class ResponseTrip {


    public String status;
    public String reason;

    @SerializedName("data")
    public List<Trip> trips;


    @ExcludeSerialization
    public String time;

    public ResponseTrip() {
    }


    private ResponseTrip(String status, String reason, List<Trip> trips) {
        this.status = status;
        this.reason = reason;
        this.trips = trips;
    }
}
