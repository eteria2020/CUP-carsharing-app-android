package it.sharengo.eteria.data.models;

import java.util.List;

import it.sharengo.eteria.data.common.ExcludeSerialization;

public class ResponseGooglePlace {

    public List<GooglePlace> results;

    public ResponseGooglePlace() {
    }

    private ResponseGooglePlace(List<GooglePlace> results) {
        this.results = results;
    }
}

