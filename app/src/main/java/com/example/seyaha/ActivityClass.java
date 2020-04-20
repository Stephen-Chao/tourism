package com.example.seyaha;

import java.io.Serializable;

public class ActivityClass implements Serializable {

    public String nameZH;
    public String nameEN;
    public int cost;
    public double time;

    public ActivityClass(String nameZh, String nameEn, int cost, double time) {
        this.nameZH = nameZh;
        this.nameEN = nameEn;
        this.cost = cost;
        this.time = time;
    }

    public ActivityClass() {
    }

}
