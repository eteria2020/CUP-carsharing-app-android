package it.sharengo.eteria.data.models;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import it.sharengo.eteria.data.common.ExcludeSerialization;

public class ResponseCar {

    public enum ErrorType{
        noError,
        generic,
        status,
        reservation,
        trip

    }

    public String status;
    public String reason;
    public Car data; //In caso di openTrip data è nullo

    @ExcludeSerialization
    public String time;

    public ResponseCar() {
    }

    private ResponseCar(String status, String reason, Car data) {
        this.status = status;
        this.reason = reason;
        this.data = data;
    }


    public ErrorType splitMessages(){
        try {
            String[] messages = reason.split("-");
            Bundle causes = new Bundle();
            ErrorType response = ErrorType.noError;

            if (messages.length >= 3) {
                for (int i = 0; i < messages.length; i++) {
                    messages[i]=messages[i].trim();
                    String[] buffer = messages[i].split(":");
                    if(buffer.length>=2) {
                        buffer[buffer.length - 2]= buffer[buffer.length - 2].trim();
                        String key = buffer[buffer.length - 2];
                        key = key.toLowerCase();
                        buffer[buffer.length - 1]=buffer[buffer.length - 1].trim();
                        buffer[buffer.length - 1] = buffer[buffer.length - 1].toLowerCase();
                        Boolean status = buffer[buffer.length - 1].equals("true");
                        causes.putBoolean(key, status);
                    }
                }
                response = getError(causes);
            }

            return response;

        }catch (Exception e){
            Log.e("BOMB","Exception while handling server error",e);
            return null;
        }
    }

    public static ErrorType splitMessages(String reason){
        try {
            JSONObject jsonResponse = new JSONObject(reason);
            reason=jsonResponse.getString("reason");

            String[] messages = reason.split("-");
            Bundle causes = new Bundle();
            ErrorType response = ErrorType.noError;

            if (messages.length >= 3) {
                for (int i = 0; i < messages.length; i++) {
                    messages[i]=messages[i].trim();
                    String[] buffer = messages[i].split(":");
                    if(buffer.length>=2) {
                        buffer[buffer.length - 2]= buffer[buffer.length - 2].trim();
                        String key = buffer[buffer.length - 2];
                        key = key.toLowerCase();
                        buffer[buffer.length - 1]=buffer[buffer.length - 1].trim();
                        buffer[buffer.length - 1] = buffer[buffer.length - 1].toLowerCase();
                        Boolean status = buffer[buffer.length - 1].equals("true");
                        causes.putBoolean(key, status);
                    }
                }
                response = getError(causes);
            }

            return response;

        }catch (Exception e){
            Log.e("BOMB","Exception while handling server error",e);
            return null;
        }
    }

    private static ErrorType getError(Bundle response){
        ErrorType error = ErrorType.generic;
        if(response!=null)
            for(String key : response.keySet()){
                if(response.getBoolean(key)){
                    switch (key){

                        case "status":
                            error = ErrorType.status;
                            break;
                        case "reservation":
                            error = ErrorType.reservation;
                            break;
                        case "trip":
                            error = ErrorType.trip;
                            break;
                        default:
                            error = ErrorType.generic;

                    }
                }
            }
        return error;
    }

}


/*
*
* {"plate":"ED93147","manufactures":"Xindayang Ltd.","model":"ZD 80","label":"/","active":true,"int_cleanliness":"clean","ext_cleanliness":"clean","notes":"1929","longitude":"9.24313","latitude":"45.51891","damages":["Paraurti posteriore","Porta sin","Led anteriore dx"],"battery":73,"frame":null,"location":"0101000020E61000005C5A0D897B7C22409FC893A46BC24640","firmware_version":"V4.6.3","software_version":"0.104.10","Mac":null,"imei":"861311004706528","last_contact":"2017-05-13T10:36:02.000Z","last_location_time":"2017-05-13T10:36:02.000Z","busy":false,"hidden":false,"rpm":0,"speed":0,"obc_in_use":0,"obc_wl_size":65145,"km":6120,"running":false,"parking":false,"status":"operative","soc":73,"vin":null,"key_status":"OFF","charging":false,"battery_offset":0,"gps_data":{"time":"13/05/2017 12:35:49","fix_age":20,"accuracy":1.4199999570846558,"change_age":20,"satellites":10},"park_enabled":false,"plug":false,"fleet_id":1,"fleets":{"id":1,"label":"Milano"}}*/