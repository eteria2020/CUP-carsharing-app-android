package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class City implements Serializable {

    @SerializedName("tid")
    public String id;

    public String name;
    public Media media;
    public Information informations;
    public boolean favourites;

    public City() {
    }

    public City(String id, String name, Media media, Information informations) {
        this.id = id;
        this.name = name;
        this.media = media;
        this.informations = informations;
    }
}
