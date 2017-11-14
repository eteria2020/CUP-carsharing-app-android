package it.sharengo.eteria.data.models;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import it.sharengo.eteria.data.common.ExcludeSerialization;

public class ResponsePutReservation {


    public enum ErrorType{
        noError,
        generic,
        status,
        reservation,
        limit,
        trip,
        unauthorized

    }

    public String status;
    public String reason;

    @SerializedName("data")
    public Reservation reservation;


    @ExcludeSerialization
    public String time;

    public ResponsePutReservation() {
    }


    private ResponsePutReservation(String status, String reason, Reservation reservation) {
        this.status = status;
        this.reason = reason;
        this.reservation = reservation;
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

    private ErrorType getError(Bundle response){
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
                        case "limit":
                            error = ErrorType.limit;
                            break;
                        case "trip":
                            error = ErrorType.trip;
                            break;
                        case "unauthorized":
                            error = ErrorType.unauthorized;
                            break;
                        default:
                            error = ErrorType.generic;

                    }
                }
            }
        return error;
    }
}
