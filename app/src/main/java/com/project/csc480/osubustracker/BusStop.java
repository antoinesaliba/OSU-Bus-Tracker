package com.project.csc480.osubustracker;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by antoinesaliba on 2/25/15.
 */
public class BusStop {
    String name;
    LatLng coordinates;
    LatLng alertPosition;
    Integer notificationId;
    boolean notification;

    public BusStop(String n, LatLng coordinates, LatLng alertPosition, Integer notificationId){
        this.name = n;
        this.coordinates = coordinates;
        this.alertPosition = alertPosition;
        this.notificationId = notificationId;
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

    public LatLng getAlertPosition() {
        return alertPosition;
    }

    public Integer getNotificationId() {
        return notificationId;
    }
}
