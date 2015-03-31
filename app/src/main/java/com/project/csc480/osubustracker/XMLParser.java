package com.project.csc480.osubustracker;

/**
 * Created by antoinesaliba on 3/23/15.
 * Edited by Lucas Neubert on 3/30/15.
 */

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.*;

public class XMLParser extends AsyncTask<String, Void, String> {
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    GoogleMap map;
    Document doc;
    final String urlBlueRoute = "http://moxie.cs.oswego.edu/~osubus/busResponseAPI.xml";
    String url;
    private final Handler handler = new Handler();
    Marker vehicleMarker;
    int id;
    double lat, lon;

    String vehicleName;


    public XMLParser(GoogleMap mMap, Marker vehicleMarker, String vehicleName) throws ParserConfigurationException, IOException, SAXException {
        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
        this.vehicleMarker = vehicleMarker;
        this.vehicleName = vehicleName;
        map = mMap;
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

            Log.i("XMLParser", "Parsing " + vehicleName);

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
            }


/*          try {
                Thread.sleep(5000); // Wait 5 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/


            //Log.i("USER INPUT", id+" ");
            //Log.i("USER INPUT", lat+" ");
            //Log.i("USER INPUT", lon+" ");

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result) {

        vehicleMarker.setPosition(new LatLng(lat, lon));



        try {
            new XMLParser(map, vehicleMarker, vehicleName).execute();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
