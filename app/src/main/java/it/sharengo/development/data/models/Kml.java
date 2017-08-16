package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import it.sharengo.development.data.common.ExcludeSerialization;

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
