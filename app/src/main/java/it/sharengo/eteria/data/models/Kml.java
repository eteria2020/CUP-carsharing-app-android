package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Kml implements Serializable {

    /*@SerializedName("areaUse")
    public String area;*/

    //public List<List<List<Float>>> coordinates;

    @SerializedName("1")
    String uno;

    public Kml() {
    }

    private Kml(/*List<List<List<Float>>> coordinates,*/ /*String area*/ String uno) {
        /*this.coordinates = coordinates;*/
        /*this.area = area;*/
        this.uno = uno;
    }
}
