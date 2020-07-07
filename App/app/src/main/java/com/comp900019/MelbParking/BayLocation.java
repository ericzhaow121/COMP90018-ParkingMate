package com.comp900019.MelbParking;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bay_location_table")
public class BayLocation {

    @PrimaryKey
    @ColumnInfo(name = "bay_id")
    public int bayID;

    public double latitude;

    public double longitude;

    public BayLocation(int bayID, double latitude, double longitude) {
        this.bayID = bayID;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
