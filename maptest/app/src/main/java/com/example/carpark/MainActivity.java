package com.example.carpark;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String LABLE = "bugs";
    private LinearLayout board;
    private TextView tv;
    private DataManagement rData;
    private DatabaseHelper dataHelper;
    private GetRestrictionMap getRestriction;
    private final LatLng currentLocation = new LatLng(-37.80467, 144.96437);

    private Handler mHander = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            board.removeView(tv);
            if (message.what == 101) {
                List<ParkingSpot> ps = (List<ParkingSpot>) message.obj;
                StringBuilder sb = new StringBuilder();
                int count = 0;
                for (ParkingSpot parkingSpot : ps) {
                    sb.append(count+": "+parkingSpot.getBay_id() + " currently status: " + parkingSpot.getState());
                    sb.append("\n");
                    count++;
                }
                tv.setText(sb);
                board.addView(tv);
            }else if(message.what == 102){
                StringBuilder sb = new StringBuilder();
                ParkingSpot ps = (ParkingSpot) message.obj;
                Log.i("LDA",ps.getBay_id()+" "+ps.getState());
//                HashMap<ParkingSpot, HashMap<String, String>> temp = getClosestSpot.getDistanceSpot(currentLocation, ps, DistanceCalculation.Mode.WALKING);
                if(ps==null){
                    tv.setText("There has no such data");
                }else{
                    sb.append(ps.getBay_id()+" current status: "+ps.getState());
                    sb.append("\n");
                    Log.i(LABLE, getRestriction.toString());
                    sb.append(getRestriction.getCurrentRestriction(Integer.parseInt(ps.getBay_id())).getDescription());
//                    sb.append("\n");
//                    sb.append(temp.get(ps).get("duration")+" mins, distance: "+temp.get(ps).get("distance"));
                    tv.setText(sb);
                }
                board.addView(tv);
            }else if(message.what == 103){
                HashMap<ParkingSpot, HashMap<String, String>> recommend = (HashMap<ParkingSpot, HashMap<String, String>>) message.obj;
                StringBuilder sb = new StringBuilder();
                int count = 0;
                for(ParkingSpot spot: recommend.keySet()){
                    String distance = recommend.get(spot).get("distance");
                    String duration = recommend.get(spot).get("duration");
                    sb.append(count+": "+spot.getBay_id() + " current Status: "+spot.getState());
                    sb.append("\n");
                    sb.append("walking time: "+duration+" mints, distance: "+distance+" miles");
                    sb.append("\n");
                    sb.append("--------------------------------------------------------");
                    sb.append("\n");
                    count++;
                }
                tv.setText(sb);
                board.addView(tv);
            }
            return false;
        }
    });
    private List<ParkingSpot> spotLists;
    private GetClosestSpot getClosestSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        board = findViewById(R.id.textPart);
        tv = new TextView(this);
        dataHelper = new DatabaseHelper(this);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("parkingData/On-street_Car_Park_Bay_Restrictions.csv")));
            getRestriction = new GetRestrictionMap(br);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rData = DataManagement.getInstance(dataHelper);
        spotLists = rData.retriveAllData();
        getClosestSpot = new GetClosestSpot();
        findViewById(R.id.refresh).setOnClickListener(this);
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EditText view = findViewById(R.id.SearchBox);
                        String text = view.getText().toString();
                        Message msg = Message.obtain();
                        ParkingSpot ps = rData.retriveData(text);
                        msg.obj = ps;
                        msg.what = 102;
                        mHander.sendMessage(msg);
                    }
                }).start();
            }
        });
        findViewById(R.id.recommand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        long endTime = 0;
                        getClosestSpot.getRecommendSpot(spotLists, currentLocation, 6);
                        HashMap<ParkingSpot, HashMap<String, String>> recom = getClosestSpot.getDurationMap();
                        endTime = System.currentTimeMillis();
                        long timeneeded =  ((startTime - endTime) /1000);
                        Log.i("helloooooo", "done: "+timeneeded+"s");
                        Message msg = Message.obtain();
                        msg.obj = recom;
                        msg.what = 103;
                        mHander.sendMessage(msg);
                    }
                }).start();
            }
        });
        //set a new thread keep updating parking spot data
        rData.updatingData();
    }

    @Override
    public void onClick(View view) {
        initalData();
    }

    private void initalData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //get all parking spot information
                spotLists = rData.retriveAllData();
                Message message = Message.obtain();
                message.obj = spotLists;
                message.what = 101;
                //send data to main thread
                mHander.sendMessage(message);
            }
        }).start();
    }
}
