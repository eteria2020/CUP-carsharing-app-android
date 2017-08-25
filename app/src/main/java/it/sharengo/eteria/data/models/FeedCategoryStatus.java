package it.sharengo.development.data.models;

import java.io.Serializable;

public class FeedCategoryStatus implements Serializable {

    public String published;


    public FeedCategoryStatus() {
    }

    public FeedCategoryStatus(String published) {
        this.published = published;
    }
}
