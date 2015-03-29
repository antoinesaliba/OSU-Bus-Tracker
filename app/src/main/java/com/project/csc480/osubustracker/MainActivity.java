package com.project.csc480.osubustracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;

import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;


public class MainActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private final Handler handler = new Handler();

    RouteHighlighter highlighter;

    AlertDialog aD = null;
    boolean finish = false;

    XMLParser parser;
    int c = 0;

    /***** Navigation Drawer Attributes *****/
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;
    // used to store app title
    private CharSequence mTitle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    Circle circle;
    boolean firstTime = true;

    /**********/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        // If there is no network connection, execute the following
        if(!isConnected()) {
            // Start the reconnect Activity
            startActivity(new Intent(MainActivity.this, Reconnect.class));
            finish();
        // if there is a network connection, continue on
        } else {
            setUpMapIfNeeded();
            // Getting reference to SupportMapFragment of the activity_main
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            if (savedInstanceState == null) {
                // on first time display view for first nav item
                displayView(0);
            }
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK)
                                .setTitle("Create Notification")
                                .setMessage("Would like to get a notification when the bus is close to " + marker.getTitle() + "?")
                                .setPositiveButton(R.string.createNotification, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // create notification...
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });


            Log.i("MainActivity", "Setup passed...");
        }
    }

    public void initialize() {
        setUpDrawerNavigation();

        /**********/
        //Creating the Blue Route object and loading the route points and the bus stops
        final BusRoute blueRoute = new BusRoute("blueRoute");
        blueRoute.loadRoute();

        //Creating the Green Route object and loading the route points and the bus stops
        final BusRoute greenRoute = new BusRoute("greenRoute");
        greenRoute.loadRoute();


        final Vehicle blueRouteVehicle = new Vehicle("blueRoute");
        blueRouteVehicle.loadMapPosition(); //temporary
    }

    //Christian's Code
    public boolean isConnected() {
        ConnectivityManager cM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    /*
     * Creates an AlertDialog that tells the user to connect to the internet
     */
    /*public void displayReconnect() {
        aD = new AlertDialog.Builder(this).create();
        aD.setTitle("No Connection");
        aD.setMessage("Please make sure you are connected to the internet before running Centroz.");
        aD.show();
        SystemClock.sleep(8000);
        aD.dismiss();
        finish();
        System.exit(0);
    }*/


    private void setUpDrawerNavigation() {

        /*** DRAWER ****/
        mTitle = mDrawerTitle = getTitle();
        // load slide menu items -> Bus Routes names
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Routes
        // Blue Route
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Green Route
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],navMenuIcons.getResourceId(0, -1)));
        // 1A
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2],navMenuIcons.getResourceId(0, -1)));


        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                //changes to settings page
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_schedule:
                Uri uriUrl = Uri.parse("http://www.centro.org/Schedules-Oswego.aspx");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_overflow).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {

        //Creating the Blue Route object and loading the route points and the bus stops
        final BusRoute blueRoute = new BusRoute("blueRoute");
        blueRoute.loadRoute();

        //Creating the Green Route object and loading the route points and the bus stops
        final BusRoute greenRoute = new BusRoute("greenRoute");
        greenRoute.loadRoute();


        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                changeRoute(mMap, blueRoute, false);
                circle = createCircle(mMap);
                try {
                    new XMLParser(circle).execute();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                changeRoute(mMap, greenRoute, true);
                break;
            case 2:
                break;
            default:
                break;
        }

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }


    //*******************/

    public Circle createCircle(GoogleMap m){ //creates the bus indicator as a circle
        return mMap.addCircle(new CircleOptions()
                .center(new LatLng(0, 0)) //CAMPUS CENTER
                .radius(5)
                .strokeColor(Color.RED)
                .fillColor(Color.RED)
                .zIndex(1)); //whether it should be above or below everything else (map, other icons, etc)
    }


    public void changeRoute(GoogleMap m, BusRoute r, boolean g){ //changes the current route being highlighted on the map
        mMap.clear();
        highlighter = new RouteHighlighter(m);
        highlighter.enableRoute(m, r, g);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(isConnected()) {
            setUpMapIfNeeded();
        /*} else {
            displayReconnect();
        }*/
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
                setUpMap(mMap); //sets up the initial, basic map with nothing on it
                highlighter = new RouteHighlighter(mMap);
                //Creating the Blue Route object and loading the route points and the bus stops
                final BusRoute blueRoute = new BusRoute("blueRoute");
                blueRoute.loadRoute();
                highlighter.enableRoute(mMap, blueRoute, false); //starts the application by showing the blue route highlighted
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
