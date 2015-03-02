package com.project.csc480.osubustracker;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by antoinesaliba on 2/25/15.
 */
public class BusStop {
    String name;
    LatLng coordinates;

    public BusStop(String n, LatLng coordinates){
        this.name = n;
        this.coordinates = coordinates;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }
}
