package com.project.csc480.osubustracker;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;


public class MainActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> markerPoints;
    boolean green; //used to tell the lineOptions to make the green route line green

    private final Handler handler = new Handler();
    int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        markerPoints = new ArrayList<LatLng>();
        green = false;

        //Creating the Blue Route object and loading the route points and the bus stops
        final BusRoute blueRoute = new BusRoute("blueRoute");
        blueRoute.loadRoute();
        blueRoute.loadBusStops();
        //Creating the Green Route object and loading the route points and the bus stops
        final BusRoute greenRoute = new BusRoute("greenRoute");
        greenRoute.loadRoute();
        greenRoute.loadBusStops();

        final Vehicle blueRouteVehicle = new Vehicle("blueRoute");
        blueRouteVehicle.loadMapPosition(); //temporary

        setUpMapIfNeeded();


        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        Switch toggle = (Switch) findViewById(R.id.switch1);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                markerPoints = new ArrayList<LatLng>();
                if (isChecked) {
                    mMap.clear();
                    green = true; //used to tell the lineOptions to make the green route line green
                    enableRoute(mMap, greenRoute);

                } else {
                    mMap.clear();
                    green = false;
                    enableRoute(mMap, blueRoute);
                }
            }
        });

        final Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(43.453838, -76.540628)) //CAMPUS CENTER
                .radius(5)
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
                .zIndex(1));

        Runnable m_handlerTask ;
        m_handlerTask = new Runnable()
        {
            @Override
            public void run() {

                updateMarker(circle, blueRouteVehicle);
                handler.postDelayed(this, 5000);

            }
        };
        m_handlerTask.run();

        Log.i("MainActivity", "Setup passed...");
    }

    public void updateMarker(Circle circle, Vehicle vehicle){
        if(c!=vehicle.getMapPosition().size()) {
            circle.setCenter(vehicle.getMapPosition().get(c));
            c++;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(mMap);
                //Creating the Blue Route object and loading the route points and the bus stops
                final BusRoute blueRoute = new BusRoute("blueRoute");
                blueRoute.loadRoute();
                blueRoute.loadBusStops();
                enableRoute(mMap, blueRoute);
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    //sets the location for the map as well as how far in to zoom (right now zoom set to 15)
    private void setUpMap(GoogleMap map) {


        //map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.453838, -76.540628), (float) 14.5)); //CAMPUS CENTER
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    public void enableRoute(GoogleMap map, BusRoute route) {
        // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
        // Up to 8 waypoints are allowed in a query for non-business users
        if (markerPoints.size() >= 10) {
            return;
        }
        for(int i=0; i < route.getRoutePoints().size(); i++) {
            markerPoints.add(route.getRoutePoints().get(i));
        }

        for(int i = 0; i < route.getBusStops().size(); i++) {
            // Add new marker to the Google Map Android API V2
            map.addMarker(new MarkerOptions()
                         .position(route.getBusStops().get(i).getCoordinates())
                         .title(route.getBusStops().get(i).getName())
                         .icon(BitmapDescriptorFactory.fromResource(R.drawable.busicon)));
        }

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED and
         * for the rest of markers, the color is AZURE
         */
        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        for(int i=2;i<markerPoints.size();i++){
            LatLng point  = (LatLng) markerPoints.get(i);
            if(i==2)
             //   waypoints = "waypoints=optimize:false|";
                  waypoints = "waypoints=";
            waypoints +=  point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Error downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(6);

                if(green){
                    lineOptions.color(Color.GREEN);
                }else {
                    lineOptions.color(Color.BLUE);
                }
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}
