package it.sharengo.development.data.models;

public class Reservation {

    public int id;
    public int reservation_timestamp;
    public int timestamp_start;
    public String car_plate;
    public int length;

    public Reservation(int id, int reservation_timestamp, int timestamp_start, String car_plate, int length) {
        this.id = id;
        this.reservation_timestamp = reservation_timestamp;
        this.timestamp_start = timestamp_start;
        this.car_plate = car_plate;
        this.length = length;
    }
}

/*
* {"status":200,"reason":"","data":[{"id":1701519,"reservation_timestamp":1497199153,"timestamp_start":1497199153,"is_active":true,"car_plate":"EF64095","length":1200}],"time":1497199167}
* */
