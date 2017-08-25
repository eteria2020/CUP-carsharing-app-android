package it.sharengo.development.data.models;

import java.io.Serializable;

public class FeedColor implements Serializable {

    public String rgb;
    public String rgb_default;
    public String filterColor;
    public String enforce;

    public FeedColor() {
    }

    public FeedColor(String rgb, String rgb_default, String enforce) {
        this.rgb = rgb;
        this.rgb_default = rgb_default;
        this.enforce = enforce;
    }
}
