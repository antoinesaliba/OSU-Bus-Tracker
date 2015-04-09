package com.project.csc480.osubustracker;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
    LatLng position;
    ArrayList<LatLng>notifications=new ArrayList<>();
    NotificationMaker manager;
    Context context;



    public Vehicle(String routeName) {
        this.vehicleName = routeName;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public ArrayList<LatLng> getMapPosition() {
        return mapPosition;
    }


    public void loadMapPosition(GoogleMap mMap, Context t) {
        Marker vehicleMarker;
        context = t;
        //manager = m;

        vehicleMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Bus")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.busstopicon)));

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
        private static final int DELAY = 10000; // 10 seconds

        @Override
        public void run() {

            while (keepDoing) {
                    new XMLParser(mapAux, vehicleMarkerAux, vehicleName, position, notifications, context).execute();

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

