package com.example.seyaha;

import java.io.Serializable;
import java.util.List;

public class Place implements Serializable {

    public List<ActivityClass> activities;
    public int airQuality;
    public String categoryZH;
    public String categoryEN;
    public Cost cost;
    public String descZH;
    public String descEN;
    public int estimatedTime;
    public String imageURL;
    public int internet;
    public double latitude;
    public double longitude;
    public String nameZH;
    public String nameEN;
    public String recommendedAge;
    public int recommendedSeason;
    public int recommendedTime;
    public String voiceURL;
    public String keywords;

    public Place(List<ActivityClass> activities, int airQuality, String categoryZH, String categoryEN, Cost cost, String descZH, String descEN, int estimatedTime, String imageURL, int internet, double latitude, double longitude, String nameZH, String nameEN, String recommendedAge, int recommendedSeason, int recommendedTime, String voiceURL, String keywords) {
        this.airQuality = airQuality;
        this.categoryZH = categoryZH;
        this.categoryEN = categoryEN;
        this.cost = cost;
        this.descZH = descZH;
        this.descEN = descEN;
        this.estimatedTime = estimatedTime;
        this.imageURL = imageURL;
        this.internet = internet;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nameZH = nameZH;
        this.nameEN = nameEN;
        this.recommendedAge = recommendedAge;
        this.recommendedSeason = recommendedSeason;
        this.recommendedTime = recommendedTime;
        this.voiceURL = voiceURL;
        this.keywords = keywords;
        this.activities = activities;
    }

    public Place() { }

}
