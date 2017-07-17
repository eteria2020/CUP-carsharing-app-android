package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

public class Feed {

    @SerializedName("nid")
    public String id;

    public String title;
    public FeedCategory category;
    public FeedMedia media;
    public FeedAppearance appearance;
    public FeedInformation informations;

    public Feed() {
    }

    public Feed(String id, String title, FeedCategory category, FeedMedia media, FeedAppearance appearance, FeedInformation informations) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.media = media;
        this.appearance = appearance;
        this.informations = informations;
    }

}
