package it.sharengo.development.data.models;

import java.io.Serializable;

public class FeedColor implements Serializable {

    public String rgb;
    public String filterColor;
    public String enforce;

    public FeedColor() {
    }

    public FeedColor(String rgb, String enforce) {
        this.rgb = rgb;
        this.enforce = enforce;
    }
}
