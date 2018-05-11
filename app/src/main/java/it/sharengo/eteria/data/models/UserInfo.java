package it.sharengo.eteria.data.models;

import java.util.List;

public class UserInfo {

    public enum DisabledType {
        USER_NOT_DISABLED,
        FIRST_PAYMENT_NOT_COMPLETED,
        FAILED_PAYMENT,
        INVALID_DRIVERS_LICENSE,
        DISABLED_BY_WEBUSER,
        EXPIRED_DRIVERS_LICENSE,
        EXPIRED_CREDIT_CARD,
    }

    public int pin;
    public String name;
    public String surname;
    public boolean enabled;
    public int discount_rate;
    public int bonus;
    public String gender;
    public List<DisabledReason> disabled_reason;

    public UserInfo() {
        pin = 0;
    }


    public UserInfo(int pin, String name, String surname, boolean enabled, int discount_rate, int bonus, String gender, List<DisabledReason> disabled_reason) {
        this.pin = pin;
        this.enabled = enabled;
        this.discount_rate = discount_rate;
        this.bonus = bonus;
        this.gender = gender;
        this.disabled_reason = disabled_reason;
    }
    public DisabledType getDisabledType(){
        try {
            return disabled_reason.get(0).getReason();
        }catch (Exception e){
            return DisabledType.DISABLED_BY_WEBUSER;
        }
    }



    public class DisabledReason{
        private DisabledType reason;

        public DisabledReason(DisabledType reason) {
            this.reason = reason;
        }

        public DisabledType getReason() {
            return reason;
        }

        public void setReason(DisabledType reason) {
            this.reason = reason;
        }
    }


    /*
    * {"status":200,"reason":"","data":{"name":"Francesco","surname":"Galatro","gender":"male","country":null,"province":null,"town":"Castel San Giorgio","address":"Via Avvocato Raffaele Lanzara 9/I","zip_code":"20145","phone":"0236552737","mobile":"+393497277108","pin":8427,"discount_rate":20,"email":"francesco.galatro@gmail.com","enabled":true,"bonus":null},"time":1498064686}
    * */
}
