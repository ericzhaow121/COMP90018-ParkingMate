package com.comp900019.MelbParking;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class SpaceWithRestriction {
    @Embedded
    public ParkingSpace space;

    @Relation(parentColumn = "bayID", entityColumn = "spaceId", entity = ParkingSpaceRestriction.class)
    public List<ParkingSpaceRestriction> parkingSpaceRestrictions;
}
