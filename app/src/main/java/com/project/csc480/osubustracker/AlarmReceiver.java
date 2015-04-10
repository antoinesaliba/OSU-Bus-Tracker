package com.project.csc480.osubustracker;

/**
 * Created by rafaelamfonseca on 4/8/15.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.xml.sax.SAXException;


import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int DELAY = 10000; // 10 seconds
    private static final String TAG = "threadNotification";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String vehicleName = intent.getStringExtra("vehicleName");
        String markerTitle = intent.getStringExtra("markerTitle");
        Double lat = intent.getDoubleExtra("lat", 0);
        Double lon = intent.getDoubleExtra("lon", 0);
        Integer notificationId = intent.getIntExtra("notificationId", 0);
        Thread notificationThread = new threadNotification(vehicleName, lat, lon, context, markerTitle, notificationId);
        notificationThread.run();
    }

    public class threadNotification extends Thread {

        String vehicleName, markerTitle;
        Double lat, lon;
        Context context;
        Integer notificationId;

        public threadNotification(String vehicleName, Double lat, Double lon, Context context, String markerTitle, Integer notificationId) {
            this.vehicleName = vehicleName;
            this.lat = lat;
            this.lon = lon;
            this.context = context;
            this.markerTitle = markerTitle;
            this.notificationId = notificationId;
        }

        @Override
        public void run() {
            try {
                NotificationParser parser = new NotificationParser(vehicleName, lat, lon, context, markerTitle, notificationId);
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
