package com.comp900019.MelbParking;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointment_table", primaryKeys = {"name", "time"})
public class Appointment {

    @NonNull
    private String name;

    @NonNull
    private long time;

    @NonNull
    private String location;

    public Appointment(@NonNull String name, @NonNull long time, @NonNull String location) {
        this.name = name;
        this.time = time;
        this.location = location;
    }

    public @NonNull String getName() {
        return name;
    }

    public @NonNull long getTime() {
        return time;
    }

    public @NonNull String getLocation() {
        return location;
    }
}
