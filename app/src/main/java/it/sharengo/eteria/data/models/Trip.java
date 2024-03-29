package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

public class Trip {

    public int id;

    @SerializedName("car_plate")
    public String plate;

    @SerializedName("lat_start")
    public float latitude;

    @SerializedName("lon_start")
    public float longitude;

    @SerializedName("lat_end")
    public float latitude_end;

    @SerializedName("lon_end")
    public float longitude_end;

    public int timestamp_start;
    public int timestamp_end;

    public int km_end;
    public int km_start;

    public float total_cost;
    public boolean cost_computed;

    public boolean payable;

    /*---*/
    public int diffTime;
    public String sCost;
    public String formattedDay;
    public String formattedH;
    public String formattedEndDay;
    public String formattedEndH;
    public String sBase;
    public String addressStart;
    public String addressEnd;

    public Trip() {
    }

    public Trip(String plate, float latitude, float longitude, float latitude_end, float longitude_end, int timestamp_start, int timestamp_end, int km_end, int km_start, float total_cost, boolean cost_computed,boolean payable) {
        this.plate = plate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.latitude_end = latitude_end;
        this.longitude_end = longitude_end;
        this.timestamp_start = timestamp_start;
        this.timestamp_end = timestamp_end;
        this.km_end = km_end;
        this.km_start = km_start;
        this.total_cost = total_cost;
        this.cost_computed = cost_computed;
        this.payable = cost_computed;
    }

    public long getTimestampEndDiff(){

        return System.currentTimeMillis()/1000 - this.timestamp_end;
    }


}
