package com.comp900019.MelbParking;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppointmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Appointment appointment);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Appointment> appointments);

    @Query("DELETE FROM appointment_table")
    void deleteAll();

    @Query("SELECT * FROM appointment_table ORDER BY time, name ASC")
    LiveData<List<Appointment>> selectAll();
}
