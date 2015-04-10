package com.project.csc480.osubustracker;

/**
 * Created by antoinesaliba on 3/23/15.
 * Edited by Lucas Neubert on 3/30/15.
 */

import android.content.Context;

import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

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
import android.app.NotificationManager;

public class XMLParser extends AsyncTask<String, Void, String> {

    public static final int NOTIFICATION_ID = 1; //this can be any int
    final String urlBlueRoute = "http://moxie.cs.oswego.edu/~osubus/busResponseAPI.xml";

    GoogleMap map;
    Document doc;
    String url;
    Marker vehicleMarker;

    //XML Data
    int id;
    double lat, lon;
    LatLng position;

    String vehicleName;
    ArrayList<LatLng>notifications; //stored coordinates where the user wants a notification
    NotificationManager manager;
    Context context;

    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;

    public XMLParser(GoogleMap mMap, Marker vehicleMarker, String vehicleName, ArrayList<LatLng> notifications, Context t) {
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            this.vehicleMarker = vehicleMarker;
            this.vehicleName = vehicleName;
            map = mMap;
            context = t;
            this.notifications = notifications;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
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

            //Log.i("XMLParser", "Parsing " + vehicleName);

            doc = dBuilder.parse(new URL(url).openStream());
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("busResponse");


            Node nNode = nList.item(0);

            //Log.i("MESSAGE", "\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                id = Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent().trim());

                lat = Double.parseDouble(eElement.getElementsByTagName("lat").item(0).getTextContent().trim());

                lon = Double.parseDouble(eElement.getElementsByTagName("lon").item(0).getTextContent().trim());

                LatLng current = new LatLng(lat, lon);

            }

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkNotifications(LatLng currentPosition){
        if(notifications.isEmpty()){
            return;
        }else{
            for(int i = 0; i< notifications.size(); i++){
                System.out.println(notifications.size());
                if(((currentPosition.latitude - 0.00004) < notifications.get(i).latitude &&  notifications.get(i).latitude < (currentPosition.latitude + 0.00004))||((currentPosition.longitude - 0.00004) < notifications.get(i).longitude &&  notifications.get(i).longitude < (currentPosition.longitude + 0.00004))){
                    launchNotification();
                    notifications.remove(i);
                }
            }
        }
    }

    private void launchNotification(){
        //Building the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.notificationicon);
        builder.setContentTitle("CentrOz");
        builder.setContentText("Bus Almost Here!");

        manager = (android.app.NotificationManager) context.getSystemService(
                context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    protected void onPostExecute(String result) {
        position = new LatLng(lat, lon);
        vehicleMarker.setPosition(position);
        checkNotifications(position);
    }
}
