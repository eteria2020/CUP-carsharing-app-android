package it.sharengo.development.data.models;

import java.io.Serializable;

public class FeedVideosDefault implements Serializable {

    public String uri;

    public FeedVideosDefault() {
    }

    public FeedVideosDefault(String uri) {
        this.uri = uri;
    }
}
