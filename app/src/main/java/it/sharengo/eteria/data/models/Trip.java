package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

public class Trip {

    public int id;

    @SerializedName("car_plate")
    public String plate;

    @SerializedName("lat_start")
    public float latitude;

    @SerializedName("lon_start")
    public float longitude;

    @SerializedName("lat_end")
    public float latitude_end;

    @SerializedName("lon_end")
    public float longitude_end;

    public int timestamp_start;
    public int timestamp_end;

    public int km_end;
    public int km_start;

    public Trip() {
    }

    public Trip(String plate, float latitude, float longitude, float latitude_end, float longitude_end, int timestamp_start, int timestamp_end, int km_end, int km_start) {
        this.plate = plate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitude_end = latitude_end;
        this.longitude_end = longitude_end;
        this.timestamp_start = timestamp_start;
        this.timestamp_end = timestamp_end;
        this.km_end = km_end;
        this.km_start = km_start;
    }

}
