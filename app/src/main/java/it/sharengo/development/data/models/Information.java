package it.sharengo.development.data.models;

import java.io.Serializable;

public class Information implements Serializable {

    public AddressCity address;

    public Information() {
    }

    private Information(AddressCity address) {
        this.address = address;
    }
}
