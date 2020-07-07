package com.comp900019.MelbParking;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "parking_space_restriction")
public class ParkingSpaceRestriction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int duration;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    public int getFromDate() {
        return fromDate;
    }

    public void setFromDate(int fromDate) {
        this.fromDate = fromDate;
    }

    public int getToDate() {
        return toDate;
    }

    public void setToDate(int toDate) {
        this.toDate = toDate;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public ParkingSpaceRestriction(int spaceId, int fromDate, int toDate, String timeStart, String timeEnd, int duration) {
        this.spaceId = spaceId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.duration = duration;
    }

    @ColumnInfo(name = "spaceId")
    public int spaceId;

    public int fromDate;
    public int toDate;

    public String timeStart;
    public String timeEnd;
}
