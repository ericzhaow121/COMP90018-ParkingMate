package com.example.carpark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Parking.db";
    private static final String TABLE_NAME = "parkingSpot_table";
    private static final String col_bay_id = "Bay_id";
    private static final String col_st_marker_id = "St_marker_id";
    private static final String col_status = "Status";
    private static final String col_lat = "Latitude";
    private static final String col_lon = "Longtitude";
    private SQLiteDatabase db;
    private final String LABEL = "bugs";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+" (Bay_id INTEGER PRIMARY KEY, St_marker_id TEXT, Status TEXT, Latitude Double, Longtitude Double)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public void refreshDatabase(){
        db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
    public boolean isDatabaseExist(){
        return queryAll().size()==0?false:true;
    }
    public boolean insert(int bay_id, String st_marker_id, String status, Double lat, Double lon){
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(col_bay_id, bay_id);
        cv.put(col_st_marker_id, st_marker_id);
        cv.put(col_status, status);
        cv.put(col_lat, lat);
        cv.put(col_lon, lon);
        long result = db.insert(TABLE_NAME, null, cv);
        return result==-1?false:true;
    }
    public void update(int bay_id, String st_marker_id, String status, double lat, double lon) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(col_status, status);
        db.update(TABLE_NAME, cv, "Bay_id = ?", new String[] {Integer.toString(bay_id)});
    }
    public ParkingSpot search(String id){
        db = this.getWritableDatabase();
        Log.i(LABEL,id);
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE Bay_id = "+id, null);
        if(cursor.moveToFirst()){
            ParkingSpot ps = new ParkingSpot(cursor.getString(0),cursor.getString(1),
                    cursor.getString(2),cursor.getString(3),cursor.getString(4));
            cursor.close();
            return ps;
        }else{
            return null;
        }
    }

    public List<ParkingSpot> queryAll(){
        //retrieve all data from database
        List<ParkingSpot> psList = new ArrayList<>();
        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        while(cursor.moveToNext()){
            ParkingSpot ps = new ParkingSpot(cursor.getString(0),cursor.getString(1),
                    cursor.getString(2),cursor.getString(3),cursor.getString(4));
            psList.add(ps);
        }
        cursor.close();
        return psList;
    }


}
