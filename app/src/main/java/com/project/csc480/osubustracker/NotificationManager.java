package com.project.csc480.osubustracker;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by antoinesaliba on 4/1/15.
 */
public class NotificationManager {
    GoogleMap map;

    public NotificationManager(GoogleMap m, final Context t, final Vehicle vehicle){
        m.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                new AlertDialog.Builder(t, AlertDialog.THEME_HOLO_DARK)
                        .setTitle("Create Notification")
                        .setMessage("Would like to be notified when the bus is close to " + marker.getTitle() + "?")
                        .setPositiveButton(R.string.createNotification, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                vehicle.notifications.add(marker.getPosition());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
}
