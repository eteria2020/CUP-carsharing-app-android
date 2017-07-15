package it.sharengo.development.data.models;

public class FeedMedia {

    public FeedImages images;
    public FeedVideos videos;

    public FeedMedia() {
    }

    public FeedMedia(FeedImages images, FeedVideos videos) {
        this.images = images;
        this.videos = videos;
    }
}
