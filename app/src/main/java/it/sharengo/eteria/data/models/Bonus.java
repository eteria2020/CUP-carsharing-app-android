package it.sharengo.eteria.data.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import it.sharengo.eteria.data.common.ExcludeSerialization;

public class Bonus {

    public String type="";
    public int value;
    public boolean status;

    public Bonus() {
    }

    public Bonus(String type, int value, boolean status) {
        this.type = type;
        this.value = value;
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;

    }

    public @NonNull String getBonus() {
        return isStatus()?getType():"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            if (o != null && String.class == o.getClass()) {
                return type.equals(o);
            }else {
                return false;
            }
        }

        Bonus bonus = (Bonus) o;

        return type.equals(bonus.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
