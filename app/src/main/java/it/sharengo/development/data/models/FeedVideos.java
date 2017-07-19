package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedVideos implements Serializable {

    @SerializedName("default")
    public FeedVideosDefault feedViedeosDefault;

    public FeedVideos() {
    }

    public FeedVideos(FeedVideosDefault feedViedeosDefault) {
        this.feedViedeosDefault = feedViedeosDefault;
    }
}
