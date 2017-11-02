package it.sharengo.eteria.data.models;

import java.util.List;

public class ResponseFeedCategory {


    public String status;
    public List<FeedCategory> data;

    public ResponseFeedCategory() {
    }

    private ResponseFeedCategory(String status, List<FeedCategory> data) {
        this.status = status;
        this.data = data;
    }
}