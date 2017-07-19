package it.sharengo.development.data.models;

import java.io.Serializable;

public class FeedUri implements Serializable {

    public String uri;

    public FeedUri() {
    }

    public FeedUri(String uri) {
        this.uri = uri;
    }
}
