package com.project.csc480.osubustracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    RouteHighlighter highlighter;
    NavDrawer drawer = new NavDrawer();
    public static NotificationDataSource datasource;

    //Creating the Blue Route object
    BusRoute blueRoute = new BusRoute("blueRoute");

    //Creating the Green Route object
    BusRoute greenRoute = new BusRoute("greenRoute");

    Vehicle blueRouteVehicle = new Vehicle("blueRoute");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Loading the route points and the bus stops
        blueRoute.loadRoute();
        //Loading the route points and the bus stops
        greenRoute.loadRoute();
        datasource = new NotificationDataSource(this);
        datasource.open();
        setUpDrawerNavigation();
        checkNotification();

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

            setDefaultRoute(); //based on settings, displays the prefered route and its vehicle icon
            //Notification Maker using the AlertManager
            NotificationMaker notificationManager = new NotificationMaker(mMap, MainActivity.this, blueRouteVehicle, blueRoute);

            Log.i("MainActivity", "Setup passed...");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void checkNotification() {
        for(int i = 0; i < datasource.getAllNotifications().size(); i++) {
            Integer notificationId = datasource.getAllNotifications().get(i).getNotificationId();
            boolean notificationExists = (PendingIntent.getBroadcast(this
                                                                   , notificationId
                                                                   , new Intent(this, AlarmReceiver.class)
                                                                   , PendingIntent.FLAG_NO_CREATE) != null);
            if (!notificationExists)
            {
                datasource.deleteNotification(notificationId);
                Log.d("myTag", "DB: Lost notification deleted " + notificationId);
            }
        }
    }

    private void setUpDrawerNavigation() {

        /*** DRAWER ****/
        drawer.mTitle = drawer.mDrawerTitle = getTitle();
        // load slide menu items -> Bus Routes names
        drawer.navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        drawer.navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        drawer.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        drawer.navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Routes
        // Blue Route
        drawer.navDrawerItems.add(new NavDrawerItem(drawer.navMenuTitles[0], drawer.navMenuIcons.getResourceId(0, -1)));
        // Green Route
        drawer.navDrawerItems.add(new NavDrawerItem(drawer.navMenuTitles[1],drawer.navMenuIcons.getResourceId(0, -1)));
        // 1A
        drawer.navDrawerItems.add(new NavDrawerItem(drawer.navMenuTitles[2],drawer.navMenuIcons.getResourceId(0, -1)));


        // setting the nav drawer list adapter
        drawer.adapter = new NavDrawerListAdapter(getApplicationContext(),
                drawer.navDrawerItems);
        drawer.mDrawerList.setAdapter(drawer.adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawer.mDrawerToggle = new ActionBarDrawerToggle(this, drawer.mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ){
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(drawer.mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(drawer.mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        drawer.mDrawerLayout.setDrawerListener(drawer.mDrawerToggle);

        drawer.mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (drawer.mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                //changes to settings page
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(settingsIntent, 1);
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
        boolean drawerOpen = drawer.mDrawerLayout.isDrawerOpen(drawer.mDrawerList);
        menu.findItem(R.id.action_overflow).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        drawer.mTitle = title;
        getActionBar().setTitle(drawer.mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawer.mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawer.mDrawerToggle.onConfigurationChanged(newConfig);
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

        switch (position) {
            case 0:
                changeRoute(blueRoute, false);
                blueRouteVehicle.loadMapPosition(mMap, MainActivity.this);
                break;
            case 1:
                blueRouteVehicle.stopLoadingPosition();
                changeRoute(greenRoute, true);
                break;
            case 2:
                break;
            default:
                break;
        }

            // update selected item and title, then close the drawer
        drawer.mDrawerList.setItemChecked(position, true);
        drawer.mDrawerList.setSelection(position);
        setTitle(drawer.navMenuTitles[position]);
        drawer.mDrawerLayout.closeDrawer(drawer.mDrawerList);
    }


    public void changeRoute(BusRoute route, boolean g){ //changes the current route being highlighted on the map
        mMap.clear();
        highlighter = new RouteHighlighter(mMap);
        highlighter.enableRoute(route, g);
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

    @Override
    protected void onResume() {
        super.onResume();
        //if(isConnected()) {
        datasource.open();
        checkNotification();
        setUpMapIfNeeded();
        /*} else {
            displayReconnect();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        datasource.open();
        checkNotification();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        datasource.open();
        checkNotification();
    }

    //return from settings page
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 1:
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

                setDefaultRoute();
                setDefaultMapStyle();

                //this returns whether the user has specified repeating notifications
                boolean rNote = settings.getBoolean("rnote", false);
                //this specifies whether user wants to cleared all notifications
                clearNotifications();
                //this then gets set back to false after the clearing has been dealt with
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("cnote", false);
                editor.commit();

                break;

        }
    }

    private void setDefaultRoute(){

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        //this returns the number associated with the root, for example: 1 = blue route
        String dRoute = settings.getString("droute", "1");
        if(dRoute.equals("1")) {
            displayView(0); // Blue Route

        }
            else if(dRoute.equals("2")) {
            displayView(1); // Green Route

        }

    }

    private void setDefaultMapStyle()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //this returns the preferred map style
        String mStyle = settings.getString("mstyle", "1");

        if(mStyle.equals("1"))
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    public void clearNotifications() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean clearAll = settings.getBoolean("cnote", false);
        Integer tableSize = datasource.getAllNotifications().size();
        Integer count = datasource.getAllNotifications().size();
        if(clearAll) {
            while (tableSize != 0) {
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), datasource.getAllNotifications().get(0).getNotificationId(), alarmIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                datasource.deleteNotification(datasource.getAllNotifications().get(0).getNotificationId());
                tableSize = datasource.getAllNotifications().size();
            }
        }
        Toast.makeText(this, count + " notification(s) deleted successfully.", Toast.LENGTH_SHORT).show();
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
            }
        }
    }


    //sets the location for the map as well as how far in to zoom (right now zoom set to 15)
    private void setUpMap(GoogleMap map) {


        //map.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.453838, -76.540628), (float) 14.5)); //CAMPUS CENTER

        setDefaultMapStyle();

    }
}
