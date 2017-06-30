package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

public class Information {

    public AddressCity address;

    public Information() {
    }

    private Information(AddressCity address) {
        this.address = address;
    }
}
