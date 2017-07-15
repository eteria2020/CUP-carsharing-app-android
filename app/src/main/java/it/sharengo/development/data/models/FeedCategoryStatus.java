package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

public class FeedCategoryStatus {

    public String published;


    public FeedCategoryStatus() {
    }

    public FeedCategoryStatus(String published) {
        this.published = published;
    }
}
