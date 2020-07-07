package com.comp900019.MelbParking;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.Manifest.permission.*;

public class CalendarEventRetriever {

    private static final String TAG = "calendar_e_retriever";

    private static final String[] INSTANCE_PROJECTION = new String[] {
        Instances.EVENT_ID, Instances.BEGIN
    };
    private static final int PROJECTION_EVENT_ID_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;

    private static final String[] EVENT_PROJECTION = new String[] {
            Events.TITLE, Events.EVENT_LOCATION
    };
    private static final int PROJECTION_TITLE_INDEX = 0;
    private static final int PROJECTION_LOC_INDEX = 1;

    private static Appointment getEventDetails(Context context, long eventID, long startTime) {
        String selection = "(" + Events._ID  + " = ?)";
        String[] selectionArgs = {Long.toString(eventID)};
        Appointment appointment = null;
        if (context.checkSelfPermission(READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = context.getContentResolver().query(Events.CONTENT_URI, EVENT_PROJECTION,
                selection, selectionArgs, null);
            if (cursor != null) {
                if (BuildConfig.DEBUG && cursor.getCount() != 1) {
                    throw new AssertionError("Multiple events found with a single ID");
                }
                cursor.moveToPosition(0);

                String eventTitle = cursor.getString(PROJECTION_TITLE_INDEX);
                String eventLocation = cursor.getString(PROJECTION_LOC_INDEX);
                if (!eventLocation.isEmpty()) {
                    appointment = new Appointment(eventTitle, startTime, eventLocation);
                }

                cursor.close();
            }
        }
        return appointment;
    }

    public static ArrayList<Appointment> getInstances(Context context) {

        Calendar currentCalendar = Calendar.getInstance();
        long currentTime = currentCalendar.getTimeInMillis();

        currentCalendar.set(Calendar.HOUR_OF_DAY, 23);
        currentCalendar.set(Calendar.MINUTE, 59);
        currentCalendar.set(Calendar.SECOND, 59);
        long endTime = currentCalendar.getTimeInMillis();

        ArrayList<Appointment> appointments = new ArrayList<>();
        if (context.checkSelfPermission(READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = Instances.query(context.getContentResolver(),
                INSTANCE_PROJECTION, currentTime, endTime);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Appointment appointment = getEventDetails(context,
                        cursor.getLong(PROJECTION_EVENT_ID_INDEX),
                        cursor.getLong(PROJECTION_BEGIN_INDEX));
                    if (appointment != null) {
                        appointments.add(appointment);
                    }
                }
                cursor.close();
            }
        }
        return appointments;
    }
}

