package com.project.csc480.osubustracker;

/**
 * Created by antoinesaliba on 3/23/15.
 * Edited by Lucas Neubert on 3/30/15.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLParser extends AsyncTask<String, Void, String> {

    final String urlBlueRoute = "http://moxie.cs.oswego.edu/~osubus/blueRouteVehicle.xml";
    final String urlA1Route = "http://moxie.cs.oswego.edu/~osubus/a1RouteVehicle.xml";

    GoogleMap map;
    Document doc;
    String url;
    Marker vehicleMarker, vehicleArrow;

    //XML Data
    int id;
    double lat, lon;
    Context context;

    String vehicleName;

    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    float direction;

    public XMLParser(GoogleMap mMap, Marker vehicleMarker, /*Marker vehicleArrow,*/ String vehicleName) {
        try {
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            context = App.getContext();
            this.vehicleMarker = vehicleMarker;
            this.vehicleName = vehicleName;
//            this.vehicleArrow = vehicleArrow;
            map = mMap;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
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
            MainActivity.appNotConnected = true;
        } else {
            try {

                if (vehicleName.equals("blueRoute")) {
                    url = urlBlueRoute;
                }
                if (vehicleName.equals("walmart1A")) {
                    url = urlA1Route;
                }

                //Log.i("XMLParser", "Parsing " + vehicleName);

                doc = dBuilder.parse(new URL(url).openStream());
                doc.getDocumentElement().normalize();

                NodeList nList = doc.getElementsByTagName("busResponse");


                Node nNode = nList.item(0);

                //Log.i("MESSAGE", "\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    direction = Float.parseFloat(eElement.getElementsByTagName("hdg").item(0).getTextContent().trim());

                    id = Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent().trim());

                    lat = Double.parseDouble(eElement.getElementsByTagName("lat").item(0).getTextContent().trim());

                    lon = Double.parseDouble(eElement.getElementsByTagName("lon").item(0).getTextContent().trim());


                }

            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        vehicleMarker.setPosition(new LatLng(lat, lon));
        //vehicleMarker.setRotation(direction);
//        vehicleArrow.setPosition(new LatLng(lat, lon));
        vehicleMarker.setRotation(direction);
    }
}
