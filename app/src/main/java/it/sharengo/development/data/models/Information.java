package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Information implements Serializable {

    public AddressCity address;

    public Information() {
    }

    private Information(AddressCity address) {
        this.address = address;
    }
}
