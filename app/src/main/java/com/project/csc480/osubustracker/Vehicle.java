package com.project.csc480.osubustracker;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;




/**
 * Created by rafaelamfonseca on 3/2/15.
 * Edited by Lucas Neubert on 4/6/15
 */
public class Vehicle {

    String vehicleName;
    ArrayList<LatLng> mapPosition = new ArrayList<LatLng>();

    GoogleMap mapAux;
    Marker vehicleMarkerAux;

    boolean keepDoing = true;
    boolean paused = false;

    threadBusPosition tBusPosition;



    public Vehicle(String routeName) {
        this.vehicleName = routeName;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public ArrayList<LatLng> getMapPosition() {
        return mapPosition;
    }


    public void loadMapPosition(GoogleMap mMap) {
        Marker vehicleMarker;
        //manager = m;

/*        vehicleArrow = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Bus")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busarrow)));

        vehicleArrow.setAnchor(0.5f,0.5f);*/

        vehicleMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon)));

        vehicleMarker.setAnchor(0.5f,0.5f);
        vehicleMarker.hideInfoWindow();

        this.mapAux = mMap;
        this.vehicleMarkerAux = vehicleMarker;
//        this.vehicleArrowAux = vehicleArrow;


        this.tBusPosition = new threadBusPosition();
        this.keepDoing = true;
        this.tBusPosition.start();


    }

    public void pauseLoadingPosition() { this.paused = true; }

    public void resumeLoadingPosition() { this.paused = false; }

    public void stopLoadingPosition(){
        this.keepDoing = false;
    }

    public class threadBusPosition extends Thread {
        private static final String TAG = "threadBusPosition";
        private static final int DELAY = 10000; // 10 seconds

        @Override
        public void run() {

            while (keepDoing) {
                if(!paused){ //if app is paused, don't do the requests
                    //sends the map, the vehicle marker, vehicle name, list of user desired notificaions and main activity context
                    new XMLParser(mapAux, vehicleMarkerAux, /*vehicleArrowAux,*/ vehicleName).execute();
                    Log.d(TAG, "Getting data from " + vehicleName);
                    //When XMLParser returns lat and long, update the marker here.
                    try {
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Interrupting and stopping the bus position Thread");
                        return;
                    }
                }
            }
        }

        }
 }

