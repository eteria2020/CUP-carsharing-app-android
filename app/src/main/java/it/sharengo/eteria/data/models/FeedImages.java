package it.sharengo.eteria.data.models;

import java.io.Serializable;

public class FeedImages implements Serializable {

    public FeedUri marker;
    public FeedUri icon;
    public FeedUri image;

    public FeedImages() {
    }

    public FeedImages(FeedUri marker, FeedUri icon, FeedUri image) {
        this.marker = marker;
        this.icon = icon;
        this.image = image;
    }

}
