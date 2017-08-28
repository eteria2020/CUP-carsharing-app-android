package it.sharengo.eteria.data.models;

public class UserInfo {


    public int pin;
    public String name;
    public String surname;
    public boolean enabled;

    public UserInfo() {
    }


    public UserInfo(int pin, String name, String surname, boolean enabled) {
        this.pin = pin;
        this.enabled = enabled;
    }

    /*
    * {"status":200,"reason":"","data":{"name":"Francesco","surname":"Galatro","gender":"male","country":null,"province":null,"town":"Castel San Giorgio","address":"Via Avvocato Raffaele Lanzara 9/I","zip_code":"20145","phone":"0236552737","mobile":"+393497277108","pin":8427,"discount_rate":20,"email":"francesco.galatro@gmail.com","enabled":true,"bonus":null},"time":1498064686}
    * */
}
