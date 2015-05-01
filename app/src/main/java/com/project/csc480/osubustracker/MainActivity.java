package com.project.csc480.osubustracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends FragmentActivity {

    public static GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static RouteHighlighter highlighter;
    public static NotificationDataSource dataSource;
    public static ArrayList<Marker>notificationList;
    Vehicle currentVehicle = null;
    public static boolean n;
    //BusRoute currentRoute;

    //Creating the route objects
    public static BusRoute blueRoute = new BusRoute("blueRoute");
    BusRoute greenRoute = new BusRoute("greenRoute");
    BusRoute walmart1A = new BusRoute("walmart1A");
    BusRoute walmart1B = new BusRoute("walmart1B");
    public Activity activity = this;



    ActionBarDrawerToggle mDrawerToggle;
    ListView mDrawerList;

    private DrawerLayout mDrawerLayout;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    NavDrawerAdapter adapter;

    List<NavDrawerItem> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        n=true;
        if(isFirstTime()){
            Intent tutorialIntent = new Intent(getApplicationContext(), TutorialActivity.class);
            startActivity(tutorialIntent);
        }
        setContentView(R.layout.activity_main);


        //Loading the route points and the bus stops
        blueRoute.loadRoute();
        //Loading the route points and the bus stops
        greenRoute.loadRoute();

        walmart1A.loadRoute();

        walmart1B.loadRoute();

        dataSource = new NotificationDataSource(this);
        dataSource.open();
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
            NotificationMaker notificationManager = new NotificationMaker(mMap, MainActivity.this, blueRoute.vehicle, blueRoute);
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

    // This method checks if there is any notification on the database
    // that does not have a correspondent notification thread running.
    // If the correspondent notification thread is not found,
    // it means that the app was not able to delete the notification record
    // from the database because the app was closed.
    // This method will clean these 'lost' notifications.
    public void checkNotification() {
        for(int i = 0; i < dataSource.getAllNotifications().size(); i++) {
            Integer notificationId = dataSource.getAllNotifications().get(i).getNotificationId();
            boolean notificationExists = (PendingIntent.getBroadcast(this
                    , notificationId
                    , new Intent(this, AlarmReceiver.class)
                    , PendingIntent.FLAG_NO_CREATE) != null);
            if (!notificationExists)
            {
                dataSource.deleteNotification(notificationId);
                Log.d("myTag", "DB: Lost notification deleted " + notificationId);
            }
        }
    }
    
     private boolean isFirstTime() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firststartup = settings.getBoolean("firsttime", true);

        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firsttime", false);
        editor.commit();
        return firststartup;
    }

    private void setUpDrawerNavigation() {

        // Initializing
        dataList = new ArrayList<NavDrawerItem>();
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        // Add Drawer Item to dataList
        //dataList.add(new DrawerItem(true)); // adding a spinner to the list

        dataList.add(new NavDrawerItem("My Location", R.drawable.ic_launcher));

        dataList.add(new NavDrawerItem("Laker")); // adding a header to the list
        dataList.add(new NavDrawerItem("Blue Route", R.drawable.ic_launcher));
        dataList.add(new NavDrawerItem("Green Route", R.drawable.ic_launcher));


        dataList.add(new NavDrawerItem("To Walmart"));// adding a header to the list
        dataList.add(new NavDrawerItem("1A - via Route 104 E", R.drawable.ic_launcher));
        dataList.add(new NavDrawerItem("1B - via E Avenue", R.drawable.ic_launcher));
        dataList.add(new NavDrawerItem("1C - via E Seneca St.", R.drawable.ic_launcher));
        dataList.add(new NavDrawerItem("1D - via Birch Lane", R.drawable.ic_launcher));

        dataList.add(new NavDrawerItem("To College"));// adding a header to the list
        dataList.add(new NavDrawerItem("2A - via Route 104 W", R.drawable.ic_launcher));
        dataList.add(new NavDrawerItem("2B - via W Seneca St.", R.drawable.ic_launcher));
        dataList.add(new NavDrawerItem("2C - via W Utica St.", R.drawable.ic_launcher));
        dataList.add(new NavDrawerItem("2D - via Ellen Street", R.drawable.ic_launcher));

        adapter = new NavDrawerAdapter(this, R.layout.custom_drawer_item,
                dataList);

        mDrawerList.setAdapter(adapter);

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);


        /***************************/
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
                startActivityForResult(settingsIntent, 1);
                return true;
            case R.id.action_schedule:
                Uri uriUrl = Uri.parse("http://www.centro.org/Schedules-Oswego.aspx");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
                return true;
            case R.id.action_about_us:
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
                alertDialog.setTitle("CentrOz");
                alertDialog.setMessage("Created by:\nAlex Merluzzi, Antoine Saliba, Christian Shank, Lucas Neubert, Molly Boardman, Pranay Chapagain, Rafaela Fonseca, Scott Millspaugh");
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                    }
                });

                alertDialog.setIcon(R.drawable.notificationicon);
                alertDialog.show();


                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#17A5F7"));
                return true;
            case R.id.action_help:
                Intent tutorialIntent = new Intent(getApplicationContext(), TutorialActivity.class);
                startActivity(tutorialIntent);
                return true;
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
//        boolean drawerOpen = mDrawer.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_overflow).setVisible(!drawerOpen);
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
            if (dataList.get(position).getTitle() == null) {
                displayView(position);
            }
        }
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {

        if(position != 0)
            myCurrentLocationSettings(); // getting the info accordingly to the settings



        switch (position) {
            case 0:
                mMap.clear();
                mMap.setMyLocationEnabled(true);
                break;
            case 2:
                if(!currentVehicle.getVehicleName().equals("blueRoute"))
                    currentVehicle.stopLoadingPosition();
                changeRoute(blueRoute);
                blueRoute.vehicle.loadMapPosition(mMap);
                break;
            case 3:
                if(!currentVehicle.getVehicleName().equals("greenRoute"))
                    currentVehicle.stopLoadingPosition();
                changeRoute(greenRoute);
                //greenRoute.vehicle.loadMapPosition(mMap);
                break;
            case 5:
                if(!currentVehicle.getVehicleName().equals("walmart1A"))
                    currentVehicle.stopLoadingPosition();
                changeRoute(walmart1A);
                walmart1A.vehicle.loadMapPosition(mMap);
                break;
            case 6:
                if(!currentVehicle.getVehicleName().equals("walmart1B"))
                    currentVehicle.stopLoadingPosition();
                changeRoute(walmart1B);
                break;
            default:
                break;
        }


        if(position != 0 && (currentVehicle.getVehicleName().equals("blueRoute") || currentVehicle.getVehicleName().equals("greenRoute")))
            checkDayOfWeek();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        setTitle(dataList.get(position).getItemName());
        mDrawerLayout.closeDrawer(mDrawerList);
    }


    private void checkDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(day == Calendar.SUNDAY)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
            alertDialog.setTitle("Not Running Today");
            alertDialog.setMessage("Hey, this route doesn't run on Sundays.");
            alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //finish();
                }
            });

            alertDialog.setIcon(R.drawable.notificationicon);
            alertDialog.show();

            TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);

            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#17A5F7"));
        }

    }


    public void changeRoute(BusRoute route){ //changes the current route being highlighted on the map
        mMap.clear();
        highlighter = new RouteHighlighter(mMap);
        highlighter.enableRoute(route);
        currentVehicle = route.vehicle;
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
        dataSource.open();
        checkNotification();
        setUpMapIfNeeded();

        if(currentVehicle != null)
            currentVehicle.resumeLoadingPosition();
        /*} else {
            displayReconnect();
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataSource.open();
        checkNotification();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        dataSource.open();
        checkNotification();

        if(currentVehicle != null)
            currentVehicle.resumeLoadingPosition();
    }

    @Override
    protected void onPause (){
        super.onPause();
        currentVehicle.pauseLoadingPosition();
    }

    //return from settings page
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 1:
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

                setDefaultRoute();
                setDefaultMapStyle();
                myCurrentLocationSettings();

                //this returns whether the user has specified repeating notifications
                boolean rNote = settings.getBoolean("rnote", false);
                //this specifies whether user wants to cleared all notifications

                /*clearNotifications();*/

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
            currentVehicle = blueRoute.vehicle;
            displayView(2); // Blue Route

        }
        else if(dRoute.equals("2")) {
            currentVehicle = greenRoute.vehicle;
            displayView(3); // Green Route

        }
        else if(dRoute.equals("3")) {
            currentVehicle = walmart1A.vehicle;
            displayView(5); // walmart 1A

        }
        else if(dRoute.equals("4")) {
            currentVehicle = walmart1B.vehicle;
            displayView(6); // walmart 1A

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

    private void myCurrentLocationSettings()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        //this returns the preferred map style
        boolean cLocation = settings.getBoolean("clocation", false);

        if(cLocation)
            mMap.setMyLocationEnabled(true);
        else
            mMap.setMyLocationEnabled(false);
    }
    /*public void clearNotifications() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean clearAll = settings.getBoolean("cnote", false);
        Integer tableSize = dataSource.getAllNotifications().size();
        Integer count = dataSource.getAllNotifications().size();
        if(clearAll) {
            while (tableSize != 0) {
                Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), dataSource.getAllNotifications().get(0).getNotificationId(), alarmIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                dataSource.deleteNotification(dataSource.getAllNotifications().get(0).getNotificationId());
                tableSize = dataSource.getAllNotifications().size();
            }
        }
        Toast.makeText(this, count + " notification(s) deleted successfully.", Toast.LENGTH_SHORT).show();
    }*/

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


        
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.453838, -76.540628), (float) 14.5)); //CAMPUS CENTER
        
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                int locationBool = 0;
                try {
                    locationBool = Settings.Secure.getInt(getApplicationContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                boolean locationOn = locationBool != Settings.Secure.LOCATION_MODE_OFF;
                if(!locationOn){
                    final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle("Location services are disabled")
                            .setMessage("To center on your location you have to enable location services")
                            .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(R.drawable.notificationicon)
                            .show();

                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#17A5F7"));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#17A5F7"));
                }else{
                    Location myLocation = mMap.getMyLocation();

                   mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), (float) 14.5));
                }

                return true;
            }
        });
        setDefaultMapStyle();

    }
}
