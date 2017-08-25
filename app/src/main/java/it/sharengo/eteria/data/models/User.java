package it.sharengo.development.data.models;

public class User {

    public String username;
    public String password;
    public String token;
    public UserInfo userInfo;

    public User() {
    }

    public User(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }
}
