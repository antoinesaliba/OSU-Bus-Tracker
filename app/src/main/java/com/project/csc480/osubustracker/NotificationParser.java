package com.project.csc480.osubustracker;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class NotificationParser extends AsyncTask<String, Void, String> {
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    Document doc;
    final String urlBlueRoute = "http://moxie.cs.oswego.edu/~osubus/blueRouteVehicle.xml";
    String url;
    int id;
    double lat, lon;
    double alertLat, alertLon;
    String vehicleName, markerTitle;
    Context context;
    android.app.NotificationManager manager;
    Integer notificationId;

    public NotificationParser(String vehicleName, Double alertLat, Double alertLon, Context context
            , String markerTitle, Integer notificationId) throws ParserConfigurationException, IOException, SAXException {
        this.dbFactory = DocumentBuilderFactory.newInstance();
        this.dBuilder = dbFactory.newDocumentBuilder();
        this.vehicleName = vehicleName;
        this.alertLat = alertLat;
        this.alertLon = alertLon;
        this.context = context;
        this.markerTitle = markerTitle;
        this.notificationId = notificationId;
    }

    @Override
    protected String doInBackground(String...urls) {
        parse();
        return "Executed";
    }

    public boolean isConnected() {
        ConnectivityManager cM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cM.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null &&
                cM.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else if (cM.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null &&
                cM.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    private void parse(){
        if(!isConnected()) {
           ///RAFAELA ENTER YOUR CODE HERE
        } else {
            try {

                if (vehicleName.equals("blueRoute")) {
                    url = urlBlueRoute;
                }

                Log.i("NotificationParser", "Checking " + vehicleName + " Bus Stop: " + markerTitle + " ID " + notificationId);

                doc = dBuilder.parse(new URL(url).openStream());
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("busResponse");
                Node nNode = nList.item(0);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    id = Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent().trim());
                    lat = Double.parseDouble(eElement.getElementsByTagName("lat").item(0).getTextContent().trim());
                    lon = Double.parseDouble(eElement.getElementsByTagName("lon").item(0).getTextContent().trim());
                    if ((lat - 0.0001) < alertLat && alertLat < (lat + 0.0001) || (lon - 0.0001) < alertLon && alertLon < (lon + 0.0001)) { //temporary - will be replaced by the 'location range' comparision
                        launchNotification();
                        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), notificationId, alarmIntent, 0);
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);
                        System.out.println("Notification deleted with id: " + notificationId);
                        MainActivity.UIChange = true;
                        try {
                            // try to delete the notification record from the database.
                            // If the app is closed, it wont be able to open the database
                            // causing a NullPointerException.
                            // In this case, the app has a method checkNotification()
                            // on the MainActivity that whenever the user opens the app
                            // it checks if there is any notification record
                            // that was not deleted from the database
                            MainActivity.dataSource.deleteNotification(notificationId);
                        } catch (NullPointerException e) {
                            Log.i("deleteNotificationError", "App Closed: Unable to delete notification from the DB: " + notificationId + " " + markerTitle);
                        }

                    }
                }

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void launchNotification(){
        changeIcon(notificationId);

        //Building the Notification
        long[] vibrationPattern = {0, 500, 250, 500} ;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.notificationicon);
        builder.setContentTitle("CentrOz");
        builder.setContentText("Time to move! Your bus is close to " + markerTitle + ".");

        //Sound and vibrate in the notification
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);
        builder.setVibrate(vibrationPattern);

        /* Goes to the app when clicked on the notification*/
        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        builder.setAutoCancel(true);

        manager = (android.app.NotificationManager) context.getSystemService(
                context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }

    private void changeIcon(Integer id){

    }
}
/*

  */