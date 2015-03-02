package com.project.csc480.osubustracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by antoinesaliba on 2/25/15.
 */
public class BusRoute {

    String routeName;
    ArrayList<BusStop> busStops = new ArrayList<BusStop>();
    ArrayList<LatLng> routePoints = new ArrayList<LatLng>();;

    public BusRoute(String routeName){
        this.routeName = routeName;
    }

    public String getRouteName() {
        return routeName;
    }

    public ArrayList<BusStop> getBusStops() {
        return busStops;
    }

    public ArrayList<LatLng> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(ArrayList<LatLng> routePoints) {
        this.routePoints = routePoints;
    }

    public void setBusStops(ArrayList<BusStop> busStops) {
        this.busStops = busStops;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public void loadBusStops(){

        if(routeName.equals("blueRoute")) {
            //BLUE ROUTE BUS STOPS
            busStops.add(new BusStop("CAMPUS CENTER", new LatLng(43.453838, -76.540628)));
            busStops.add(new BusStop("MACKIN", new LatLng(43.454804, -76.53475284576416)));
            busStops.add(new BusStop("JOHNSON", new LatLng(43.45713231914716, -76.53761744499207)));
            busStops.add(new BusStop("LIBRARY", new LatLng(43.45426628708711, -76.54450535774231)));
            busStops.add(new BusStop("MARY_WALKER", new LatLng(43.455475, -76.542743)));
            busStops.add(new BusStop("SHINEMAN", new LatLng(43.454282, -76.539160)));
        } else if(routeName.equals("greenRoute")) {
            //GREEN ROUTE BUS STOPS
            busStops.add(new BusStop("CAMPUS CENTER", new LatLng(43.453838, -76.540628)));
            busStops.add(new BusStop("ROMNEY", new LatLng(43.447918, -76.534195)));
            busStops.add(new BusStop("LAKER", new LatLng(43.446368, -76.53462409973145)));
        }
    }

    public void loadRoute() {

        if(routeName.equals("blueRoute")) {
                //BLUE ROUTE HIGHLIGHTING
                routePoints.add(new LatLng(43.453838, -76.540628)); // CAMPUS_CENTER
                routePoints.add(new LatLng(43.453838, -76.540628)); // CAMPUS_CENTER
                routePoints.add(new LatLng(43.453523, -76.541181)); // CIRCLE
                routePoints.add(new LatLng(43.457295865792744, -76.53929114341736)); // RIGGS_HALL
                routePoints.add(new LatLng(43.450535, -76.549731)); // ONONDAGA
                routePoints.add(new LatLng(43.44699935247679, -76.54906511306763)); // VILLAGE
                routePoints.add(new LatLng(43.454309, -76.543996)); // PENFIELD_LIBRARY
                routePoints.add(new LatLng(43.454282, -76.539160)); // SHINEMAN

        } else if(routeName.equals("greenRoute")) {
                //GREEN ROUTE HIGHLIGHTING
                routePoints.add(new LatLng(43.453838, -76.540628)); // CAMPUS_CENTER
                routePoints.add(new LatLng(43.453838, -76.540628)); // CAMPUS_CENTER
                routePoints.add(new LatLng(43.45357312306545, -76.53239250183105)); //FIFTHAVE
                routePoints.add(new LatLng(43.447918, -76.534195)); //ROMNEY
                routePoints.add(new LatLng(43.446368, -76.53462409973145)); //LAKER

        } else {
            //return error
        }
    }
}
