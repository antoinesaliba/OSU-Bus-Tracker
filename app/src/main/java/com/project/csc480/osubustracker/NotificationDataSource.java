package com.project.csc480.osubustracker;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NotificationDataSource {

    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID
                                   ,SQLiteHelper.COLUMN_BSNAME
                                   ,SQLiteHelper.COLUMN_ROUTENAME
                                   ,SQLiteHelper.COLUMN_NOTIFICATIONID
                                   ,SQLiteHelper.COLUMN_TIME
                                  };

    public NotificationDataSource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Notification createNotification(String busStopName, String routeName
                                                            , Integer notificationId) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_BSNAME, busStopName);
        values.put(SQLiteHelper.COLUMN_ROUTENAME, routeName);
        values.put(SQLiteHelper.COLUMN_NOTIFICATIONID, notificationId);
        values.put(SQLiteHelper.COLUMN_TIME, System.currentTimeMillis());
        long insertId = database.insert(SQLiteHelper.TABLE_NOTIFICATIONS, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_NOTIFICATIONS,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Notification newNotification = cursorToNotification(cursor);
        cursor.close();
        System.out.println("Notification created with id: " + notificationId);
        return newNotification;
    }

    public void deleteNotification(Integer notificationId) {
        System.out.println("Notification deleted with id: " + notificationId);
        database.delete(SQLiteHelper.TABLE_NOTIFICATIONS, SQLiteHelper.COLUMN_NOTIFICATIONID
                + " = " + notificationId, null);
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<Notification>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_NOTIFICATIONS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Notification notification = cursorToNotification(cursor);
            notifications.add(notification);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return notifications;
    }

    private Notification cursorToNotification(Cursor cursor) {
        Notification notification = new Notification();
        notification.setId(cursor.getLong(0));
        notification.setBusStopName(cursor.getString(1));
        notification.setRouteName(cursor.getString(2));
        notification.setNotificationId(cursor.getInt(3));
        notification.setTime(cursor.getString(4));
        return notification;
    }
}