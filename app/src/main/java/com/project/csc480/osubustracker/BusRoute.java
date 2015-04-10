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

    public void loadRoute(){
        loadBusStops();
        loadRoutePoints();
    }

    public int getBusStopIndex(String busStopName) {
        for(int i = 0; i < this.getBusStops().size(); i++) {
            if(this.getBusStops().get(i).getName().equals(busStopName)) {
                return i;
            }
        }
        return -1;
    }

    public void loadBusStops(){

        if(routeName.equals("blueRoute")) {
            //BLUE ROUTE BUS STOPS
            busStops.add(new BusStop("Campus Center", new LatLng(43.453838, -76.540628),new LatLng(43.4539526, -76.5405475), 1));
            busStops.add(new BusStop("Mackin", new LatLng(43.454804, -76.53475284576416),new LatLng(43.4549406, -76.5348609), 2));
            busStops.add(new BusStop("Johnson", new LatLng(43.45713231914716, -76.53761744499207),new LatLng(43.4571612, -76.5372736), 3));
            busStops.add(new BusStop("Library", new LatLng(43.45426628708711, -76.54450535774231),new LatLng(43.454331, -76.5434586), 4));
            busStops.add(new BusStop("Mary Walker", new LatLng(43.455475, -76.542743),new LatLng(43.4554639, -76.5424972), 5));
            busStops.add(new BusStop("Shineman", new LatLng(43.454282, -76.539160),new LatLng(43.4548819, -76.5390339), 6));
            busStops.add(new BusStop("Village", new LatLng(43.44699935247679, -76.54906511306763),new LatLng(43.4469709, -76.5488651), 7));

        }
        else if(routeName.equals("greenRoute")) {
            //GREEN ROUTE BUS STOPS
            busStops.add(new BusStop("Campus Center", new LatLng(43.453838, -76.540628),new LatLng(43.453838, -76.540628), 8));
            busStops.add(new BusStop("Romney", new LatLng(43.447918, -76.534195),new LatLng(43.447918, -76.534195), 9));
            busStops.add(new BusStop("Laker", new LatLng(43.446368, -76.53462409973145),new LatLng(43.446368, -76.53462409973145), 10));
        }
    }

    public void loadRoutePoints() {

        if(routeName.equals("blueRoute")) {
                //BLUE ROUTE HIGHLIGHTING
                routePoints.add(new LatLng(43.453838, -76.540628)); // CAMPUS_CENTER // Origin
                routePoints.add(new LatLng(43.453838, -76.540628)); // CAMPUS_CENTER // Destination
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
