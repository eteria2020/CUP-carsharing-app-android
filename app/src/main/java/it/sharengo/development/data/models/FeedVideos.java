package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

public class FeedVideos {

    @SerializedName("default")
    public FeedVideosDefault feedViedeosDefault;

    public FeedVideos() {
    }

    public FeedVideos(FeedVideosDefault feedViedeosDefault) {
        this.feedViedeosDefault = feedViedeosDefault;
    }
}
