package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

public class Trip {

    public int id;

    @SerializedName("car_plate")
    public String plate;

    @SerializedName("lat_start")
    public float latitude;

    @SerializedName("lon_start")
    public float longitude;

    public Trip() {
    }

    public Trip(String plate, float latitude, float longitude) {
        this.plate = plate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
