package it.sharengo.eteria.data.models;

public class GoogleLeg {

    public GoogleDistance distance;
    public GoogleDuration duration;

    public GoogleLeg() {
    }

    private GoogleLeg(GoogleDistance distance, GoogleDuration duration) {
        this.distance = distance;
        this.duration = duration;
    }
}