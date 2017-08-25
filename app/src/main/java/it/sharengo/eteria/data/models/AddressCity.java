package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

public class AddressCity {

    @SerializedName("lng")
    public float longitude;

    @SerializedName("lat")
    public float latitude;

    public AddressCity() {
    }

    private AddressCity(float longitude, float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
