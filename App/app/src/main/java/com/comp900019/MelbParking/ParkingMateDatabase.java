package com.comp900019.MelbParking;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.lang.ref.WeakReference;

@Database(entities = {BayLocation.class, ParkingSpace.class, ParkingSpaceRestriction.class, Appointment.class}, version = 5, exportSchema = false)
public abstract class ParkingMateDatabase extends RoomDatabase {

    public abstract AppointmentDao appointmentDao();
    public abstract ParkingSpaceDAO parkingSpaceDAO();

    private static volatile ParkingMateDatabase INSTANCE;
    private static WeakReference<Context> contextReference;

    private static RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateBayInfoDbAsync(INSTANCE).execute(contextReference.get());
        }
    };

    static ParkingMateDatabase getDataBase(final Context context) {
        if (INSTANCE == null) {

            synchronized (ParkingMateDatabase.class) {
                if (INSTANCE == null) {
                    contextReference = new WeakReference<>(context);
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        ParkingMateDatabase.class, "bay_info_database")
                            .fallbackToDestructiveMigration()
                        .addCallback(callback).build();
                }
            }
        }
        return INSTANCE;
    }
}
