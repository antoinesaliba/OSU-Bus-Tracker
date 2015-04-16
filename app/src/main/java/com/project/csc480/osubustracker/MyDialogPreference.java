package com.project.csc480.osubustracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.Toast;

/**
 * Created by Scott on 4/16/2015.
 */
public class MyDialogPreference extends DialogPreference{

    public MyDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int button){

        if(button == DialogInterface.BUTTON_POSITIVE) {

            System.out.println("VERYPOSITIVE");
            SharedPreferences.Editor editor = getEditor();
            editor.putBoolean("cnote", true);
            editor.commit();
            clearNotifications();
            Toast.makeText(this.getContext(),"Notifications Deleted!", Toast.LENGTH_SHORT).show();

        }else{
            System.out.println("NEGATIVEVERY");
        }
    }

    public void clearNotifications() {
        MainActivity.datasource = new NotificationDataSource(this.getContext());
        MainActivity.datasource.open();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean clearAll = settings.getBoolean("cnote", false);
        Integer tableSize = MainActivity.datasource.getAllNotifications().size();
        if(clearAll) {
            while (tableSize != 0) {
                Intent alarmIntent = new Intent(this.getContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getContext(), MainActivity.datasource.getAllNotifications().get(0).getNotificationId(), alarmIntent, 0);
                AlarmManager alarmManager = (AlarmManager) this.getContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                MainActivity.datasource.deleteNotification(MainActivity.datasource.getAllNotifications().get(0).getNotificationId());
                tableSize = MainActivity.datasource.getAllNotifications().size();
            }
        }
    }
}


