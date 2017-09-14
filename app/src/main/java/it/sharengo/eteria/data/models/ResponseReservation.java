package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import it.sharengo.eteria.data.common.ExcludeSerialization;

public class ResponseReservation {


    public String status;
    public String reason;

    @SerializedName("data")
    public List<Reservation> reservations;


    @ExcludeSerialization
    public String time;

    public ResponseReservation() {
    }


    private ResponseReservation(String status, String reason, List<Reservation> reservations) {
        this.status = status;
        this.reason = reason;
        this.reservations = reservations;
    }
}
