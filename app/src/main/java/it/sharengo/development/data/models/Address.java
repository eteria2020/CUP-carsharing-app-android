package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import it.sharengo.development.data.common.ExcludeSerialization;

public class Address {

    @SerializedName("place_id")
    public String id;

    @SerializedName("lon")
    public float longitude;

    @SerializedName("lat")
    public float latitude;

    public String display_name;

    public String friendly;



    @ExcludeSerialization
    public boolean favourite;

    public Address() {
    }

    private Address(String id, float longitude, float latitude, String display_name) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.display_name = display_name;
    }

    private Address(String friendly, float longitude, float latitude) {
        this.friendly = friendly;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
