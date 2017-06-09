package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import it.sharengo.development.data.common.ExcludeSerialization;

import static android.R.attr.id;

public class User {

    public String username;
    public String password;
    public String token;
    public int pin;

    public User() {
    }

    public User(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }

    public User(String username, String password, String token, int pin) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.pin = pin;
    }
}
