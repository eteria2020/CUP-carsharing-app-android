package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OsmPlace {

    @SerializedName("place_id")
    private String placeId;
    @SerializedName("licence")
    private String licence;
    @SerializedName("osm_type")
    private String osmType;
    @SerializedName("osm_id")
    private String osmId;
    @SerializedName("boundingbox")
    private List<String> boundingbox = null;
    @SerializedName("polygonpoints")
    private List<List<String>> polygonpoints = null;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lon")
    private String lon;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("class")
    private String _class;
    @SerializedName("type")
    private String type;
    @SerializedName("importance")
    private Double importance;
    @SerializedName("address")
    private Address address;


    public OsmPlace(String placeId, String licence, String osmType, String osmId, List<String> boundingbox, List<List<String>> polygonpoints, String lat, String lon, String displayName, String _class, String type, Double importance, Address address) {
        super();
        this.placeId = placeId;
        this.licence = licence;
        this.osmType = osmType;
        this.osmId = osmId;
        this.boundingbox = boundingbox;
        this.polygonpoints = polygonpoints;
        this.lat = lat;
        this.lon = lon;
        this.displayName = displayName;
        this._class = _class;
        this.type = type;
        this.importance = importance;
        this.address = address;
    }
    public OsmPlace(){

    }

    public OsmPlace(String displayName,String lat, String lon){
        this.displayName = displayName;
        this.lat = lat;
        this.lon = lon;
    }
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getLicence() {
        return licence;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    public String getOsmType() {
        return osmType;
    }

    public void setOsmType(String osmType) {
        this.osmType = osmType;
    }

    public String getOsmId() {
        return osmId;
    }

    public void setOsmId(String osmId) {
        this.osmId = osmId;
    }

    public List<String> getBoundingbox() {
        return boundingbox;
    }

    public void setBoundingbox(List<String> boundingbox) {
        this.boundingbox = boundingbox;
    }

    public List<List<String>> getPolygonpoints() {
        return polygonpoints;
    }

    public void setPolygonpoints(List<List<String>> polygonpoints) {
        this.polygonpoints = polygonpoints;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getClass_() {
        return _class;
    }

    public void setClass_(String _class) {
        this._class = _class;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getImportance() {
        return importance;
    }

    public void setImportance(Double importance) {
        this.importance = importance;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}

