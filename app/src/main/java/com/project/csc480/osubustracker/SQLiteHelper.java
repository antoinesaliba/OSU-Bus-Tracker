package com.project.csc480.osubustracker;

/**
 * Created by rafaelamfonseca on 4/10/15.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NOTIFICATIONS = "notifications";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BSNAME = "busStopName";
    public static final String COLUMN_ROUTENAME = "routeName";
    public static final String COLUMN_NOTIFICATIONID = "notificationId";
    public static final String COLUMN_TIME = "time";

    private static final String DATABASE_NAME = "centroz.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTIFICATIONS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_BSNAME + " text not null,"
            + COLUMN_ROUTENAME + " text not null,"
            + COLUMN_NOTIFICATIONID + " integer not null,"
            + COLUMN_TIME + " int not null"
            + ");";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
    }

}