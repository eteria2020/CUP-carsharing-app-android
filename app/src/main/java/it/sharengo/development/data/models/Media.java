package it.sharengo.development.data.models;

import java.io.Serializable;

public class Media implements Serializable {

    public Images images;

    public Media() {
    }

    public Media(Images images) {
        this.images = images;
    }
}
