package com.comp900019.MelbParking;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

@Entity(tableName = "parking_space")
public class ParkingSpace {

    public ParkingSpace() {}


    @PrimaryKey
    @ColumnInfo(name="bayID")
    public int bayID;

    public  boolean occupied;
    public double latitude;
    public double longitude;


}
