package it.sharengo.eteria.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FeedCategory implements Serializable {

    @SerializedName("tid")
    public String id;

    public String name;
    public FeedCategoryStatus status;
    public FeedMedia media;
    public FeedAppearance appearance;

    public FeedCategory() {
    }

    public FeedCategory(String id, String name, FeedCategoryStatus status, FeedMedia media, FeedAppearance appearance) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.media = media;
        this.appearance = appearance;
    }

    /*
    * {
		"tid": "1",
		"name": "Shopping",
		"status": {
			"published": "1"
		},
		"media": {
			"images": {
				"marker": {
					"uri": "http:\/\/universo-sharengo.thedigitalproject.it\/sites\/default\/files\/assets\/images\/markers\/puntatore-shop-generico_0.png"
				},
				"icon": {
					"uri": "http:\/\/universo-sharengo.thedigitalproject.it\/sites\/default\/files\/assets\/images\/icons\/sng-icona-shop-100.png"
				},
				"image": {
					"uri": "http:\/\/universo-sharengo.thedigitalproject.it\/sites\/default\/files\/assets\/images\/sng-icona-shop-100.png"
				}
			},
			"videos": {
				"default": {
					"uri": "http:\/\/universo-sharengo.thedigitalproject.it\/sites\/default\/files\/assets\/videos\/video_demo.mp4"
				}
			}
		},
		"appearance": {
			"color": {
				"rgb": "#3aa652"
			}
		}
	}
    *
    * */
}
