package com.project.csc480.osubustracker;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by antoinesaliba on 2/25/15.
 */
public class BusStop {
    String name;
    LatLng coordinates;

    public BusStop(String n, double lat, double lon){
        name = n;
        coordinates = new LatLng(lat, lon);
    }
}
