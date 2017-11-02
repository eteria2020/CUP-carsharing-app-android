package it.sharengo.eteria.data.models;

import java.util.List;

public class ResponseFeed {


    public String status;
    public List<Feed> data;

    public ResponseFeed() {
    }

    private ResponseFeed(String status, List<Feed> data) {
        this.status = status;
        this.data = data;
    }
}