package it.sharengo.eteria.data.models;

import java.io.Serializable;

public class FeedMedia implements Serializable {

    public FeedImages images;
    public FeedVideos videos;

    public FeedMedia() {
    }

    public FeedMedia(FeedImages images, FeedVideos videos) {
        this.images = images;
        this.videos = videos;
    }
}
