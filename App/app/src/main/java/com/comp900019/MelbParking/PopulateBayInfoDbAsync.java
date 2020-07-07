package com.comp900019.MelbParking;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class PopulateBayInfoDbAsync extends AsyncTask<Context, Void, Void> {

    private final ParkingSpaceDAO psDao;
    private final AppointmentDao appointmentDao;

    private ArrayList<SpaceWithRestriction> spaces = new ArrayList<SpaceWithRestriction>();

    private ArrayList<ParkingSpaceRestriction> allRestrictions = new ArrayList<ParkingSpaceRestriction>();
    private ArrayList<ParkingSpace> allSpaces = new ArrayList<ParkingSpace>();


    PopulateBayInfoDbAsync(ParkingMateDatabase db) {
        psDao = db.parkingSpaceDAO();
        appointmentDao = db.appointmentDao();
    }


    private boolean getParkingSpaceInformation() {
        try {
            String jsonString = "";
            String jsonStringRestriction = "";

            URL sensorURL = new URL("https://data.melbourne.vic.gov.au/api/views/vh2v-4nfs/rows.json");
            HttpsURLConnection connection = (HttpsURLConnection) sensorURL.openConnection();
            URL restrictionURL = new URL("https://data.melbourne.vic.gov.au/api/views/ntht-5rk7/rows.json");
            HttpsURLConnection connection2 = (HttpsURLConnection) restrictionURL.openConnection();


            if (connection.getResponseCode() == 200 && connection2.getResponseCode() == 200) {
                Log.d("DAO", "Connected");
                InputStream restrictInput = connection2.getInputStream();
                BufferedReader restrictBr = new BufferedReader(new InputStreamReader(restrictInput));
                String line = null;
                while ((line = restrictBr.readLine()) != null) {
                    jsonStringRestriction = jsonStringRestriction.concat(line);
                }

                JSONObject restrictJSON = new JSONObject(jsonStringRestriction);
                final JSONArray restrictDataArray = restrictJSON.getJSONArray("data");

                Thread processRestrictions = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Log.d("DATA_RET", "Starting Collecting Restrictions");
                            for (int i = 0; i < restrictDataArray.length(); i++) {
                                JSONArray dataRow = restrictDataArray.getJSONArray(i);
                                int bayID = Integer.parseInt(dataRow.getString(8));
                                ArrayList<ParkingSpaceRestriction> restrictions = new ArrayList<>();
                                for (int j = 0; j < 6; j++) {
                                    if (dataRow.isNull(j + 10)) {
                                        break;
                                    } else {
                                        int duration = dataRow.getInt(j + 22);
                                        String endTime = dataRow.getString(j + 34);
                                        int fromDay = Integer.parseInt(dataRow.getString(j + 46));
                                        String startTime = dataRow.getString(j + 52);
                                        int toDay = Integer.parseInt(dataRow.getString(j + 58));

                                        // Convert to ISO date of week
                                        if (fromDay == 0) {
                                            fromDay = 7;
                                        }
                                        if (toDay == 0) {
                                            toDay = 7;
                                        }
                                        ParkingSpaceRestriction restriction = new ParkingSpaceRestriction(bayID, fromDay, toDay, startTime, endTime, duration);
                                        restrictions.add(restriction);
                                        allRestrictions.add(restriction);
                                    }
                                }
                                if (bayID == 567) {
                                    Log.d("SPACES ", Integer.toString(restrictions.size()));
                                }
                            }

                            psDao.deleteAllRestrictions();
                            psDao.insertSpaceRestriction(allRestrictions);

                            Log.d("DATA_RET", "FINISHED RESTRICTIONS");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                processRestrictions.start();


                InputStream input = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                line = null;
                while ((line = br.readLine()) != null) {
                    jsonString = jsonString.concat(line);
                }

                JSONObject sensorJson = new JSONObject(jsonString);
                JSONArray dataArray = sensorJson.getJSONArray("data");


                for (int i = 0; i < dataArray.length(); i++) {
                    JSONArray dataRow = dataArray.getJSONArray(i);
                    int bayID = Integer.parseInt(dataRow.getString(8));
                    boolean isOccupied = dataRow.getString(10).equals("Present");
                    double latitude = Double.parseDouble(dataRow.getString(12));
                    double longitude = Double.parseDouble(dataRow.getString(13));

                    SpaceWithRestriction space = new SpaceWithRestriction();

                    space.space = new ParkingSpace();

                    space.space.occupied = isOccupied;
                    space.space.latitude = latitude;
                    space.space.longitude = longitude;
                    space.space.bayID = bayID;

                    allSpaces.add(space.space);
                }
                Log.d("DATA_RET", "Using Space DAO");

                psDao.insertAllParkingSpace(allSpaces);
                Log.d("DATA_RET", "Finished All Spaces");

            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void populateAppointments(Context context) {
        appointmentDao.insertAll(CalendarEventRetriever.getInstances(context));
    }

    @Override
    protected Void doInBackground(final Context... params) {
        appointmentDao.deleteAll();




        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                getParkingSpaceInformation();
            }
        });
        thread2.start();

        populateAppointments(params[0]);

        return null;
    }

}
