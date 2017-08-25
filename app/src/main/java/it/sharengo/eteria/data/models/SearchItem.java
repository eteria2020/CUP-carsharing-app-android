package it.sharengo.development.data.models;

import it.sharengo.development.data.common.ExcludeSerialization;

public class SearchItem {

    public String display_name;
    public String name;
    public String type;
    public float longitude;
    public float latitude;

    @ExcludeSerialization
    public boolean favourite;

    public SearchItem() {
    }

    public SearchItem(String display_name, String type, float longitude, float latitude) {
        this.display_name = display_name;
        this.type = type;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public SearchItem(String display_name, String type){
        this.display_name = display_name;
        this.type = type;
    }

    public SearchItem(String name, String display_name, String type){
        this.name = name;
        this.display_name = display_name;
        this.type = type;
    }

    public SearchItem(String name, String display_name, String type, float longitude, float latitude){
        this.name = name;
        this.display_name = display_name;
        this.type = type;
    }
}


/*
*
* {"plate":"ED93147","manufactures":"Xindayang Ltd.","model":"ZD 80","label":"/","active":true,"int_cleanliness":"clean","ext_cleanliness":"clean","notes":"1929","longitude":"9.24313","latitude":"45.51891","damages":["Paraurti posteriore","Porta sin","Led anteriore dx"],"battery":73,"frame":null,"location":"0101000020E61000005C5A0D897B7C22409FC893A46BC24640","firmware_version":"V4.6.3","software_version":"0.104.10","Mac":null,"imei":"861311004706528","last_contact":"2017-05-13T10:36:02.000Z","last_location_time":"2017-05-13T10:36:02.000Z","busy":false,"hidden":false,"rpm":0,"speed":0,"obc_in_use":0,"obc_wl_size":65145,"km":6120,"running":false,"parking":false,"status":"operative","soc":73,"vin":null,"key_status":"OFF","charging":false,"battery_offset":0,"gps_data":{"time":"13/05/2017 12:35:49","fix_age":20,"accuracy":1.4199999570846558,"change_age":20,"satellites":10},"park_enabled":false,"plug":false,"fleet_id":1,"fleets":{"id":1,"label":"Milano"}}*/