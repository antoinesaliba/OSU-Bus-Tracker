package com.project.csc480.osubustracker;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

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

        vehicleMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Bus")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon)));

        this.mapAux = mMap;
        this.vehicleMarkerAux = vehicleMarker;


        this.tBusPosition = new threadBusPosition();
        this.keepDoing = true;
        this.tBusPosition.start();


    }

    public void stopLoadingPosition(){
        this.keepDoing = false;
    }

    public class threadBusPosition extends Thread {
        private static final String TAG = "threadBusPosition";
        private static final int DELAY = 5000; // 5 seconds

        @Override
        public void run() {

            while (keepDoing) {
                Log.i(TAG, "doing work in the bus position Thread");

                try {
                    new XMLParser(mapAux, vehicleMarkerAux, vehicleName).execute();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }

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
