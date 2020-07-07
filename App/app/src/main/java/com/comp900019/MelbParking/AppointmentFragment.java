package com.comp900019.MelbParking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 */
public class AppointmentFragment extends Fragment {

    private static final int READ_CALENDAR = 0;

    private ParkingMateViewModel myViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.appointment_recycler_view);
        final AppointmentAdapter adapter = new AppointmentAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (getActivity().checkSelfPermission(Manifest.permission.READ_CALENDAR)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_CALENDAR}, READ_CALENDAR);
        }

        myViewModel = ViewModelProviders.of(this).get(ParkingMateViewModel.class);
        myViewModel.getAllAppointments().observe(this, new Observer<List<Appointment>>() {
            @Override
            public void onChanged(List<Appointment> appointments) {
                adapter.setAppointments(appointments);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
