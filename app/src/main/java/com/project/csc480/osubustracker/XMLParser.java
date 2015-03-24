package com.project.csc480.osubustracker;

/**
 * Created by antoinesaliba on 3/23/15.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.*;

public class XMLParser extends AsyncTask<String, Void, String> {
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    Document doc;
    final String url = "http://moxie.cs.oswego.edu/~osubus/busResponseAPI.xml";
    int id;
    double lat, lon;


    public XMLParser() throws ParserConfigurationException, IOException, SAXException {
        dbFactory = DocumentBuilderFactory.newInstance();
        dBuilder = dbFactory.newDocumentBuilder();
        }

    @Override
    protected String doInBackground(String...urls) {
        try {
            doc = dBuilder.parse(new URL(url).openStream());
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("busResponse");


            Node nNode = nList.item(0);

            Log.i("MESSAGE", "\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                id = Integer.parseInt(eElement.getElementsByTagName("id").item(0).getTextContent().trim());

                lat = Double.parseDouble(eElement.getElementsByTagName("lat").item(0).getTextContent().trim());

                lon = Double.parseDouble(eElement.getElementsByTagName("lon").item(0).getTextContent().trim());
            }

            //Log.i("USER INPUT", id+" ");
            //Log.i("USER INPUT", lat+" ");
            //Log.i("USER INPUT", lon+" ");

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Executed";
    }
}
