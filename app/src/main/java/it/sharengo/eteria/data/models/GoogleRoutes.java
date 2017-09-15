package it.sharengo.eteria.data.models;

import java.util.List;

public class GoogleRoutes {

    public GoogleOverviewPolyline overview_polyline;
    public List<GoogleLeg> legs;

    public GoogleRoutes() {
    }

    private GoogleRoutes(GoogleOverviewPolyline overview_polyline, List<GoogleLeg> legs) {
        this.overview_polyline = overview_polyline;
        this.legs = legs;
    }
}