package com.example.toni.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Toni on 22.9.2017..
 */

public class LocationsDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "locations.db";
    private static final String TABLE_LOCATIONS = "LOCATIONS";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_MOVIE_LAT = "LAT";
    private static final String COLUMN_MOVIE_LNG = "LNG";

    public LocationsDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LOCATIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MOVIE_LAT + " BIG INT, "
                + COLUMN_MOVIE_LNG + " BIG INT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    public void addLocation(double lat, double lng) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_LAT, lat);
        values.put(COLUMN_MOVIE_LNG, lng);
        db.insert(TABLE_LOCATIONS, null, values);
        db.close();
    }

    public Cursor getAllLocations(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_LOCATIONS,
                null);
    }

    public void deleteAllLocations()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, null, null);
        db.close();
    }
}
