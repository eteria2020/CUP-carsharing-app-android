package it.sharengo.eteria.data.models;

import java.io.Serializable;

public class FeedAppearance implements Serializable {

    public FeedColor color;

    public FeedAppearance() {
    }

    public FeedAppearance(FeedColor color) {
        this.color = color;
    }
}
