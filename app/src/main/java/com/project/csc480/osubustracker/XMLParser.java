package com.project.csc480.osubustracker;

/**
 * Created by antoinesaliba on 3/23/15.
 * Edited by Lucas Neubert on 3/30/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

public class XMLParser extends AsyncTask<String, Void, String> {
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    GoogleMap map;
    Document doc;
    final String urlBlueRoute = "http://moxie.cs.oswego.edu/~osubus/busResponseAPI.xml";
    String url;
   // private final Handler handler = new Handler();
    Marker vehicleMarker;
    int id;
    double lat, lon;
    LatLng position;
    Context context;

    String vehicleName;
    ArrayList<LatLng>notifications=new ArrayList<>();


    public XMLParser(GoogleMap mMap, Marker vehicleMarker, String vehicleName, LatLng position, ArrayList<LatLng> notifications, final Context t) throws ParserConfigurationException, IOException, SAXException {
        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
        this.vehicleMarker = vehicleMarker;
        this.vehicleName = vehicleName;
        map = mMap;
        this.position = position;
        this.notifications = notifications;
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

                checkNotifications(current);

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
                if((currentPosition.latitude - 0.00004) < notifications.get(i).latitude &&  notifications.get(i).latitude < (currentPosition.latitude + 0.00004)){
                    System.out.println("YOOOOOOOO");
                }
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        position = new LatLng(lat, lon);
        vehicleMarker.setPosition(position);
        System.out.println(vehicleMarker.getPosition());
    }
}
