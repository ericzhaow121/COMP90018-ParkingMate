package com.comp900019.MelbParking;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class OnClickSpaceInfoBottomSheet extends BottomSheetDialogFragment {
    public static OnClickSpaceInfoBottomSheet newInstance() {
        return new OnClickSpaceInfoBottomSheet();
    }

    public static final String latitude_key = "key_latitude";
    public static final String longitude_key = "key_longitude";

    private double latitude;
    private double longitude;
    private static boolean locationPermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_map_select, container, false);
        Bundle args = getArguments();

        String duration = args.getString(ParkFragment.DURATION_KEY);
        String durationText;

        latitude = args.getDouble(ParkFragment.LATITUDE_KEY);
        longitude = args.getDouble(ParkFragment.LONGITUDE_KEY);
        locationPermission = true;


        ParkingPropertiesCalculator calc = new ParkingPropertiesCalculator();

        double parkingRate = calc.parkingCost(latitude, longitude);

        TextView rateView = view.findViewById(R.id.rate_view);
        rateView.setText(String.format("%.2f $", parkingRate));

        Geocoder geocode = new Geocoder(getContext(), Locale.getDefault());


        Button parkButton = view.findViewById(R.id.park_button);
        parkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (OnClickSpaceInfoBottomSheet.locationPermission) {
                    Intent intent = new Intent(getContext(), NavigationActivity.class);
                    intent.putExtra(latitude_key, latitude);
                    intent.putExtra(longitude_key, longitude);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "Location Permissions Required", Toast.LENGTH_SHORT).show();
                }
            }
        });


        try {
            List<Address> addressList = geocode.getFromLocation(latitude, longitude, 1);
            TextView addressView = (TextView)view.findViewById(R.id.address);

            if (addressList != null && addressList.size() >= 1) {
                addressView.setText(addressList.get(0).getAddressLine(0));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (duration != null) {
            Log.d("ParkingLen", duration.toString());

        } else {
            Log.d("ParkingLen", "Null Duration");
        }

        if (duration == null || duration == "null") {
            durationText = "P";
        } else if (duration == "5") {
            durationText = "5Mins";
        } else if (duration == "10") {
            durationText = "10Mins";
        } else if (duration == "15") {
            durationText = "1/4P";
        } else if (duration == "30") {
            durationText = "1/2P";
        } else if (Integer.parseInt(duration) % 60 == 0) {
            durationText = Integer.toString(Integer.parseInt(duration) / 60) + "P";
        } else {
            durationText = duration + "Mins";
        }

        TextView durationView = (TextView)view.findViewById(R.id.ParkingRestriction);
        durationView.setText(durationText);

        return view;
    }


}
