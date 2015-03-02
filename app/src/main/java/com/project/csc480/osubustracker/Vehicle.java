package com.project.csc480.osubustracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by rafaelamfonseca on 3/2/15.
 */
public class Vehicle {

    String vehicleName;
    ArrayList<LatLng> mapPosition = new ArrayList<LatLng>();

    public Vehicle(String routeName) {
        this.vehicleName = routeName;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public ArrayList<LatLng> getMapPosition() {
        return mapPosition;
    }

    //temporary - it will be replaced with the value from the XML file
    public void loadMapPosition() {
        if(vehicleName.equals("blueRoute")) {
            //BLUE ROUTE BUS STOPS
            mapPosition.add(new LatLng(43.453838, -76.540628)); // CAMPUS_CENTER
            mapPosition.add(new LatLng(43.454804, -76.53475284576416)); //MACKIN
            mapPosition.add(new LatLng(43.45713231914716, -76.53761744499207)); //JOHNSON
            mapPosition.add(new LatLng(43.454309, -76.543996)); // PENFIELD_LIBRARY
            mapPosition.add(new LatLng(43.450535, -76.549731)); // ONONDAGA
            mapPosition.add(new LatLng(43.44699935247679, -76.54906511306763)); // VILLAGE
            mapPosition.add(new LatLng(43.454309, -76.543996)); // PENFIELD_LIBRARY
            mapPosition.add(new LatLng(43.454282, -76.539160)); // SHINEMAN
            mapPosition.add(new LatLng(43.453838, -76.540628)); // CAMPUS_CENTER
        }
    }
}
