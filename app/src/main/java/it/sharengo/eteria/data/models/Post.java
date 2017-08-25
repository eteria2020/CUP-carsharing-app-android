package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import it.sharengo.development.data.common.ExcludeSerialization;

public class Post {

    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String title;
    @SerializedName("body")
    public String body;
    @SerializedName("userId")
    public int userId;

    @ExcludeSerialization
    public boolean favourite;

    public Post() {
    }

    private Post(int id, String title, String body, int userId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userId = userId;
    }
}
