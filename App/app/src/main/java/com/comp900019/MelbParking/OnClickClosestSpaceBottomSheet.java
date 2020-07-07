package com.comp900019.MelbParking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class OnClickClosestSpaceBottomSheet extends OnClickSpaceInfoBottomSheet {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        TextView header = view.findViewById(R.id.textView);
        header.setText(R.string.closest_spot_details);
        return view;
    }
}