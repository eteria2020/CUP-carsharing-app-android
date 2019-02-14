package it.sharengo.eteria.data.models;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import it.sharengo.eteria.data.common.ExcludeSerialization;

public class Car {

    @SerializedName("plate")
    public String id;

    public String manufactures;
    public String model;

    @SerializedName(value="lon", alternate={"longitude"})
    public float longitude;

    @SerializedName(value="lat", alternate={"latitude"})
    public float latitude;

    public String status;
    public boolean parking;

    public boolean busy;

    public boolean active;

    public boolean hidden;

    public String software_version;

    @SerializedName("battery")
    public int autonomy;

    public List<Bonus> bonus;

    @ExcludeSerialization
    public boolean favourite;

    public Car() {
    }

    public Car(String id, String manufactures, String model, float longitude, float latitude, String status, int autonomy, boolean parking, List<Bonus> bonus, String software_version) {
        this.id = id;
        this.manufactures = manufactures;
        this.model = model;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.autonomy = autonomy;
        this.parking = parking;
        this.bonus = bonus;
        this.software_version = software_version;
    }

    public Car(String id, String manufactures, String model, float longitude, float latitude, String status, int autonomy, boolean parking,boolean busy,boolean hidden, boolean active,  List<Bonus> bonus, String software_version) {
        this.id = id;
        this.manufactures = manufactures;
        this.model = model;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.autonomy = autonomy;
        this.parking = parking;
        this.busy = busy;
        this.bonus = bonus;
        this.active = active;
        this.hidden = hidden;
        this.software_version = software_version;
    }

    public Car(String id, float longitude, float latitude) {
        this.id = id;
        this.manufactures = manufactures;
        this.model = model;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.autonomy = autonomy;
    }

    public @NonNull ArrayList<Bonus> getValidBonus(){
        ArrayList<Bonus> result =new ArrayList<Bonus>();
        if(bonus!=null){
            for (Bonus b:bonus) {
                if(!b.getBonus().equals(""))
                    result.add(b);
            }
        }
        return result;
    }

    public boolean haveActiveBonus(String type){
        ArrayList<Bonus> bonuses = getValidBonus();
        return bonuses.contains(new Bonus(type,5,true));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;

        Car car = (Car) o;

        return id != null ? id.equals(car.id) : car.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Location getLocation(){
        Location loc =  new Location("");
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        return loc;
    }

    public int getVersionObc(String software_version){
        int software_versionInt = 0;
        software_version = software_version.replaceFirst(".", ",");
        software_versionInt = Integer.parseInt(software_version.substring(0,software_version.indexOf(".")));
        return software_versionInt;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + id + '\'' +
                ", manufactures='" + manufactures + '\'' +
                ", model='" + model + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", status='" + status + '\'' +
                ", parking=" + parking +
                ", busy=" + busy +
                ", active=" + active +
                ", hidden=" + hidden +
                ", autonomy=" + autonomy +
                ", bonus=" + bonus +
                ", favourite=" + favourite +
                ", software_version=" + software_version +
                '}';
    }
}




/*
*
* {"plate":"ED93147","manufactures":"Xindayang Ltd.","model":"ZD 80","label":"/","active":true,"int_cleanliness":"clean","ext_cleanliness":"clean","notes":"1929","longitude":"9.24313","latitude":"45.51891","damages":["Paraurti posteriore","Porta sin","Led anteriore dx"],"battery":73,"frame":null,"location":"0101000020E61000005C5A0D897B7C22409FC893A46BC24640","firmware_version":"V4.6.3","software_version":"0.104.10","Mac":null,"imei":"861311004706528","last_contact":"2017-05-13T10:36:02.000Z","last_location_time":"2017-05-13T10:36:02.000Z","busy":false,"hidden":false,"rpm":0,"speed":0,"obc_in_use":0,"obc_wl_size":65145,"km":6120,"running":false,"parking":false,"status":"operative","soc":73,"vin":null,"key_status":"OFF","charging":false,"battery_offset":0,"gps_data":{"time":"13/05/2017 12:35:49","fix_age":20,"accuracy":1.4199999570846558,"change_age":20,"satellites":10},"park_enabled":false,"plug":false,"fleet_id":1,"fleets":{"id":1,"label":"Milano"}}*/