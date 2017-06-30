package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import it.sharengo.development.data.common.ExcludeSerialization;

import static android.R.attr.id;

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
