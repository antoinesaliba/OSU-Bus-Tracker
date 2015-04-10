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

import java.util.List;

/**
 * Created by antoinesaliba on 4/1/15.
 */
public class NotificationMaker {
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    public NotificationMaker(GoogleMap m, final Context t, final Vehicle vehicle, final BusRoute route) {
        m.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                boolean notificationExists = checkIfNotificationExists(marker.getTitle());
                if (!notificationExists) {
                    new AlertDialog.Builder(t, AlertDialog.THEME_HOLO_DARK)
                            .setTitle("Create Notification")
                            .setMessage("Would like to be notified when the bus is close to " + marker.getTitle() + "?")
                            .setPositiveButton(R.string.createNotification, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //vehicle.notifications.add(marker.getPosition());

                                    //Notification Maker using the AlertManager (to be tested)
                                    if (!marker.getTitle().equals("Bus")) {
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

                                        MainActivity.datasource.createNotification(marker.getTitle(), route.getRouteName(), notificationId);
                                        pendingIntent = PendingIntent.getBroadcast(t, notificationId, alarmIntent, 0);
                                        manager = (AlarmManager) t.getSystemService(t.ALARM_SERVICE);
                                        int interval = 5000;
                                        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    new AlertDialog.Builder(t, AlertDialog.THEME_HOLO_DARK)
                            .setTitle("Delete Notification")
                            .setMessage("A notification for this bus stop already exists. Would you like to delete it?")
                            .setPositiveButton(R.string.deleteNotification, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Integer notificationId = route.getBusStops().get(route.getBusStopIndex(marker.getTitle())).getNotificationId();
                                    MainActivity.datasource.deleteNotification(notificationId);
                                    Intent alarmIntent = new Intent(t, AlarmReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(t.getApplicationContext(), notificationId, alarmIntent, 0);
                                    AlarmManager alarmManager = (AlarmManager) t.getSystemService(t.ALARM_SERVICE);
                                    alarmManager.cancel(pendingIntent);
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
            }
        });
    }
    public boolean checkIfNotificationExists(String busStopName) {

        List<com.project.csc480.osubustracker.Notification> notifications = MainActivity.datasource.getAllNotifications();
        for(int i = 0; i < notifications.size(); i++) {
            if(notifications.get(i).getBusStopName().equals(busStopName)) {
                return true;
            }
        }
    return false;
    }


}
