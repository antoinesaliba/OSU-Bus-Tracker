package com.project.csc480.osubustracker;

import android.app.AlarmManager;
import android.app.AlertDialog;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
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

                // check if a notification already exists for that particular bus stop
                if (!notificationExists && !marker.getTitle().equals("Bus") && MainActivity.displayPosition != 0) {
                    //t.setTheme(R.style.MyTheme);
                    final AlertDialog alertDialog = new AlertDialog.Builder(t, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle("Create Notification")
                            .setMessage("Would like to be notified when the bus is close to " + marker.getTitle() + "?")
                            .setPositiveButton(R.string.createNotification, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.busstopicon_orange));
                                        Toast.makeText(t, "You will be notified when bus is close!", Toast.LENGTH_SHORT).show();

                                        BusStop busStop = route.getBusStops().get(route.getBusStopIndex(marker.getTitle()));
                                        busStop.notification = true;
                                        // each bus stop has a particular alertPosition
                                        LatLng alertPosition = busStop.getAlertPosition();
                                        route.getBusStops().get(route.getBusStopIndex(marker.getTitle())).notification = true;
                                        Double lat = alertPosition.latitude;
                                        Double lon = alertPosition.longitude;
                                        // each bus stop has a unique notificationId to keep track of the notifications
                                        Integer notificationId = busStop.getNotificationId();
                                        String previousBusStop = busStop.getPreviousBusStop();

                                        Intent alarmIntent = new Intent(t, AlarmReceiver.class);
                                        alarmIntent.putExtra("vehicleName", vehicle.getVehicleName());
                                        alarmIntent.putExtra("markerTitle", marker.getTitle());
                                        alarmIntent.putExtra("lat", lat);
                                        alarmIntent.putExtra("lon", lon);
                                        alarmIntent.putExtra("notificationId", notificationId);
                                        alarmIntent.putExtra("previousBusStop", previousBusStop);

                                        //creates the notification record on the database
                                        MainActivity.dataSource.createNotification(marker.getTitle(), route.getRouteName(), notificationId);
                                        //creates the notification thread each interval seconds to check the bus position
                                        int interval = 5000;
                                        pendingIntent = PendingIntent.getBroadcast(t, notificationId, alarmIntent, 0);
                                        manager = (AlarmManager) t.getSystemService(t.ALARM_SERVICE);
                                        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(R.drawable.notificationicon)
                            .show();

                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#17A5F7"));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#17A5F7"));
                } else if (notificationExists && !marker.getTitle().equals("Bus") && MainActivity.displayPosition != 0){
                    final AlertDialog alertDialog = new AlertDialog.Builder(t, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle("Delete Notification")
                            .setMessage("A notification for this bus stop already exists. Would you like to delete it?")
                            .setPositiveButton(R.string.deleteNotification, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.busstopicon));
                                    Toast.makeText(t, "Notification Deleted!", Toast.LENGTH_SHORT).show();

                                    // each bus stop has a unique notificationId to keep track of the notifications
                                    Integer notificationId = route.getBusStops().get(route.getBusStopIndex(marker.getTitle())).getNotificationId();

                                    // deletes the notification record from the database
                                    MainActivity.dataSource.deleteNotification(notificationId);

                                    // stops the notification thread that was checking the bus position
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
                            .setIcon(0)
                            .show();
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#17A5F7"));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#17A5F7"));
                }
                else if(MainActivity.displayPosition == 0){

/*
                   AlertDialog alertDialog = new AlertDialog.Builder(t, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
                    alertDialog.setTitle("Bus Stop Info");
                    alertDialog.setMessage("In this Bus Stop you can get the:\n" +route.getRouteName()  );
                    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                        }
                    });

                    alertDialog.setIcon(R.drawable.notificationicon);
                    alertDialog.show();

                    alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#17A5F7"));
*/
                }
            }
        });
    }

    public static boolean checkIfNotificationExists(String busStopName) {

        List<com.project.csc480.osubustracker.Notification> notifications = MainActivity.dataSource.getAllNotifications();
        for(int i = 0; i < notifications.size(); i++) {
            if(notifications.get(i).getBusStopName().equals(busStopName)) {
                return true;
            }
        }
    return false;
    }


}
