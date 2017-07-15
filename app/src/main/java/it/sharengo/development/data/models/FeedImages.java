package it.sharengo.development.data.models;

public class FeedImages {

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
