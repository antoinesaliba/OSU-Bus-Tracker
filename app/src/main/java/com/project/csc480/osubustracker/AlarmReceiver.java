package com.project.csc480.osubustracker;

/**
 * Created by rafaelamfonseca on 4/8/15.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import org.xml.sax.SAXException;


import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String vehicleName = intent.getStringExtra("vehicleName");
        String markerTitle = intent.getStringExtra("markerTitle");
        Double lat = intent.getDoubleExtra("lat", 0);
        Double lon = intent.getDoubleExtra("lon", 0);
        Integer notificationId = intent.getIntExtra("notificationId", 0);
        String previousBusStop = intent.getStringExtra("previousBusStop");

        // every time the AlarmReceiver is called (based on the interval defined on the NotificationMaker class)
        // it will create a notification thread to check if the bus is close the to bus stop alert position
        Thread notificationThread = new threadNotification(vehicleName, lat, lon, context, markerTitle, notificationId, previousBusStop);
        notificationThread.run();
    }

    public class threadNotification extends Thread {

        String vehicleName, markerTitle;
        Double lat, lon;
        Context context;
        Integer notificationId;
        String previousBusStop;

        public threadNotification(String vehicleName, Double lat, Double lon, Context context
                                 , String markerTitle, Integer notificationId, String previousBusStop) {
            this.vehicleName = vehicleName;
            this.lat = lat;
            this.lon = lon;
            this.context = context;
            this.markerTitle = markerTitle;
            this.notificationId = notificationId;
            this.previousBusStop = previousBusStop;
        }

        @Override
        public void run() {
            try {
                NotificationParser parser = new NotificationParser(vehicleName, lat, lon, context
                                                                  , markerTitle, notificationId, previousBusStop);
                parser.execute();

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }
}
