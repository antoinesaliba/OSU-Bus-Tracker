package com.project.csc480.osubustracker;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
    final String urlBlueRoute = "http://moxie.cs.oswego.edu/~osubus/busResponseAPI.xml";
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

    private void parse(){
        try {

            if(vehicleName.equals("blueRoute")) {
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
                if(alertLat == lat && alertLon == lon) { //temporary - will be replaced by the 'location range' comparision
                    launchNotification();
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), notificationId, alarmIntent, 0);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);
                    System.out.println("Notification deleted with id: " + notificationId);
                    try {
                        // try to delete the notification record from the database.
                        // If the app is closed, it wont be able to open the database
                        // causing a NullPointerException.
                        // In this case, the app has a method checkNotification()
                        // on the MainActivity that whenever the user opens the app
                        // it checks if there is any notification record
                        // that was not deleted from the database
                        MainActivity.datasource.deleteNotification(notificationId);
                    } catch (NullPointerException e){
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

    private void launchNotification(){
        //Building the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.busicon);
        builder.setContentTitle("CentrOz");
        builder.setContentText("Time to move! Your bus is close to " + markerTitle + ".");

        manager = (android.app.NotificationManager) context.getSystemService(
                context.NOTIFICATION_SERVICE);
        manager.notify(notificationId, builder.build());
    }
}
