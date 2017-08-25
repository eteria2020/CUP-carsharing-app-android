package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import it.sharengo.development.data.common.ExcludeSerialization;

public class ResponsePutReservation {


    public String status;
    public String reason;

    @SerializedName("data")
    public Reservation reservation;


    @ExcludeSerialization
    public String time;

    public ResponsePutReservation() {
    }


    private ResponsePutReservation(String status, String reason, Reservation reservation) {
        this.status = status;
        this.reason = reason;
        this.reservation = reservation;
    }
}
