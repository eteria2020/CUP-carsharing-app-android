package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

public class FeedDate {

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
