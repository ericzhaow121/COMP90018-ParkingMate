package com.comp900019.MelbParking;

import android.util.Log;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;

public class AllowedDurationCalculator {


    public Integer getCurrentInterval(SpaceWithRestriction restrictedSpace) {
        List<ParkingSpaceRestriction> spaceRestrictions = restrictedSpace.parkingSpaceRestrictions;

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);


        for (ParkingSpaceRestriction psr : spaceRestrictions) {
            if (psr.fromDate <= day && psr.toDate >= day) {
                // Check the Restriction time
                if (LocalTime.now().isAfter(LocalTime.parse(psr.timeStart))
                        && LocalTime.now().isBefore(LocalTime.parse(psr.timeEnd))) {
                    // We are in between the range
                    return psr.duration;
                }
            }
        }

        // The space is unrestricted
        return null;
    }

}
