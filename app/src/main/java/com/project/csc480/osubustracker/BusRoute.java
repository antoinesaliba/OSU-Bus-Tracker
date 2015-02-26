package com.project.csc480.osubustracker;

import java.util.ArrayList;

/**
 * Created by antoinesaliba on 2/25/15.
 */
public class BusRoute {
    String name;
    ArrayList<BusStop>stops;

    public BusRoute(String n){
        name = n;
        stops = new ArrayList<BusStop>(10);
    }

    public void add(BusStop bu){ //allows for bus stop objects to be directly added to the array list
        stops.add(bu);
    }

    public void add(String name, double lat, double lon){ //creates a bus stop object from parameters and then adds it to list
        stops.add(new BusStop(name, lat, lon));
    }
}
