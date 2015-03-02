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

    private final Handler handler = new Handler();

    RouteGenerator highlighter;

    int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
                if (isChecked) {
                    changeRoute(mMap, greenRoute, true);

                } else {
                    changeRoute(mMap, blueRoute, false);
                }
            }
        });

        final Circle circle = createCircle(mMap);

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

    public Circle createCircle(GoogleMap m){
        return mMap.addCircle(new CircleOptions()
                .center(new LatLng(43.453838, -76.540628)) //CAMPUS CENTER
                .radius(5)
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
                .zIndex(1));
    }

    public void updateMarker(Circle circle, Vehicle vehicle){
        if(c!=vehicle.getMapPosition().size()) {
            circle.setCenter(vehicle.getMapPosition().get(c));
            c++;
        }
    }

    public void changeRoute(GoogleMap m, BusRoute r, boolean g){
        mMap.clear();
        highlighter = new RouteGenerator(m);
        highlighter.enableRoute(m, r, g);
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
                highlighter = new RouteGenerator(mMap);
                //Creating the Blue Route object and loading the route points and the bus stops
                final BusRoute blueRoute = new BusRoute("blueRoute");
                blueRoute.loadRoute();
                blueRoute.loadBusStops();
                highlighter.enableRoute(mMap, blueRoute, false);
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
}
