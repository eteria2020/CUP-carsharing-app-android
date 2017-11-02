package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

public class GooglePlace {

    @SerializedName("formatted_address")
    public String address;

    public GooglePlaceGeometry geometry;
    public String icon;
    public String id;
    public String name;
    public String place_id;
    public String reference;

    public GooglePlace() {
    }

    private GooglePlace(String address, GooglePlaceGeometry geometry, String icon, String name, String place_id, String reference) {
        this.address = address;
        this.geometry = geometry;
        this.icon = icon;
        this.name = name;
        this.place_id = place_id;
        this.reference = reference;
    }
}


/*
*       "formatted_address" : "Via Roma, Cesano Boscone MI, Italia",
         "geometry" : {
            "location" : {
               "lat" : 45.4422852,
               "lng" : 9.095534400000002
            },
            "viewport" : {
               "northeast" : {
                  "lat" : 45.4436341802915,
                  "lng" : 9.096883380291503
               },
               "southwest" : {
                  "lat" : 45.4409362197085,
                  "lng" : 9.094185419708499
               }
            }
         },
         "icon" : "https://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png",
         "id" : "57722bad61bdcdfeed2b3adf9ac716ddc27331d1",
         "name" : "Via Roma",
         "place_id" : "ChIJqyws-W3ChkcRojUkJfTLaBk",
         "reference" : "CmRbAAAAx-Sm6bP9-WBg92fVAiakea11ygmaXWrW1IJ5TRh_9GIj1RKnjsltkP0PgcSavaU0QmVE3XoZW-pPNCfgMWVbWpGtdeL4g3j2lSM8-drF80bbdE5xk39Z-29xUS-4yj3bEhATuxsULp-DacNcFoaLbSCQGhS0bAAFTXgV5jXhdXPBy4YX1bXDFA",
         "types" : [ "route" ]
*
* */