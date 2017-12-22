package it.sharengo.eteria.data.models;

import java.util.List;

public class ResponseSharengoMap {

    public List<Address> results;

    public ResponseSharengoMap() {
    }

    private ResponseSharengoMap(List<Address> results) {
        this.results = results;
    }
}

