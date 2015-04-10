package com.project.csc480.osubustracker;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by antoinesaliba on 4/1/15.
 */
public class NotificationMaker {
    GoogleMap map;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

  //public NotificationManager(GoogleMap m, final Context t, final Vehicle vehicle, final BusRoute route){
    public NotificationMaker(GoogleMap m, final Context t, final Vehicle vehicle){
        m.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                new AlertDialog.Builder(t, AlertDialog.THEME_HOLO_DARK)
                        .setTitle("Create Notification")
                        .setMessage("Would like to be notified when the bus is close to " + marker.getTitle() + "?")
                        .setPositiveButton(R.string.createNotification, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                vehicle.notifications.add(marker.getPosition());

                                //Notification Maker using the AlertManager (to be tested)
                                /*
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!marker.getTitle().equals("Bus")) {
                                        Toast.makeText(t, "Notification Created!", Toast.LENGTH_SHORT).show();
                                        Intent alarmIntent = new Intent(t, AlarmReceiver.class);
                                        alarmIntent.putExtra("vehicleName", vehicle.getVehicleName());
                                        alarmIntent.putExtra("markerTitle", marker.getTitle());
                                        LatLng alertPosition = route.getBusStops().get(route.getBusStopIndex(marker.getTitle())).getAlertPosition();
                                        Integer notificationId = route.getBusStops().get(route.getBusStopIndex(marker.getTitle())).getNotificationId();
                                        Double lat = alertPosition.latitude;
                                        Double lon = alertPosition.longitude;
                                        alarmIntent.putExtra("lat", lat);
                                        alarmIntent.putExtra("lon", lon);
                                        alarmIntent.putExtra("notificationId", notificationId);
                                        pendingIntent = PendingIntent.getBroadcast(t, notificationId, alarmIntent, 0);
                                        manager = (AlarmManager) t.getSystemService(t.ALARM_SERVICE);
                                        int interval = 5000;
                                        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
                                        //TO DO implement code to create a persisted notification
                                        // and a code to check if a notification is already set for this
                                        // bus stop
                                    }
                                }
                                */

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
