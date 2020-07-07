package com.comp900019.MelbParking;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class ParkingSpaceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertParkingSpace(ParkingSpace space);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertSpaceRestriction(List<ParkingSpaceRestriction> parkingSpaceRestrictions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAllParkingSpace(List<ParkingSpace> space);




    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateAll(ParkingSpace space);

    @Query("DELETE FROM parking_space")
    public abstract void deleteAll();

    @Query("DELETE FROM parking_space_restriction")
    public abstract void deleteAllRestrictions();

    @Query("SELECT * FROM parking_space")
    public abstract LiveData<List<SpaceWithRestriction>> getAllSpaces();



    public void insertParkingSpaceWithRestriction(SpaceWithRestriction psr) {
        for (ParkingSpaceRestriction restrict : psr.parkingSpaceRestrictions) {
            restrict.spaceId = psr.space.bayID;
        }
        insertSpaceRestriction(psr.parkingSpaceRestrictions);

        insertParkingSpace(psr.space);
    }

}
