package it.sharengo.development.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedInformation implements Serializable {

    public String sponsored;
    public String launch_title;
    public FeedDate date;
    public City city;
    public String location;
    public Address address;
    public String advantage_top;
    public String advantage_bottom;

    @SerializedName("abstract")
    public String abstract_text;

    public String description;

    public FeedInformation() {
    }

    public FeedInformation(String sponsored, String launch_title, FeedDate date, City city, String location, Address address, String advantage_top, String advantage_bottom, String abstract_text, String description) {
        this.sponsored = sponsored;
        this.launch_title = launch_title;
        this.date = date;
        this.city = city;
        this.location = location;
        this.address = address;
        this.advantage_top = advantage_top;
        this.advantage_bottom = advantage_bottom;
        this.abstract_text = abstract_text;
        this.description = description;
    }


    /*
    * "informations": {
			"date": {
				"default": "31-08-2017 23:59",
				"friendly": "Gioved\u00ec 31 Agosto 2017"
			},
			"city": {
				"tid": "5",
				"name": "Milano"
			},
			"location": "Mediaworld",
			"address": {
				"friendly": "Viale Certosa 29",
				"lat": "45.48951665604109",
				"lng": "9.151568412780762"
			},
			"advantage_top": "Sconto 99% TOP",
			"advantage_bottom": "Sconto 50% BOTTOM",
			"abstract": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
			"description": "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"
		}
    *
    * */
}
