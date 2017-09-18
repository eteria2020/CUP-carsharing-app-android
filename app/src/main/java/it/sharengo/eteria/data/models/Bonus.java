package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

import it.sharengo.eteria.data.common.ExcludeSerialization;

public class Bonus {

    public String type;
    public int value;
    public boolean status;

    public Bonus() {
    }

    public Bonus(String type, int value, boolean status) {
        this.type = type;
        this.value = value;
        this.status = status;
    }
}
