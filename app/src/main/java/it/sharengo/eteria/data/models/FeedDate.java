package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedDate implements Serializable {

    public String friendly;

    @SerializedName("default")
    public String date;

    public FeedDate() {
    }

    public FeedDate(String date, String friendly) {
        this.date = date;
        this.friendly = friendly;
    }

    /*
    * "date": {
				"default": "31-08-2017 23:59",
				"friendly": "Gioved\u00ec 31 Agosto 2017"
			},
    *
    * */
}
