package it.sharengo.eteria.data.models;

import java.util.List;

public class ResponseGoogleRoutes {

    public List<GoogleRoutes> routes;

    public ResponseGoogleRoutes() {
    }

    private ResponseGoogleRoutes(List<GoogleRoutes> routes) {
        this.routes = routes;
    }
}

