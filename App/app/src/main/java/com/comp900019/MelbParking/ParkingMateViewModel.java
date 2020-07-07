package com.comp900019.MelbParking;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

public class ParkingMateViewModel extends AndroidViewModel {

    private ParkingMateRepository repository;
    private LiveData<List<SpaceWithRestriction>> allParkingSpaces;

    private LiveData<List<Appointment>> allAppointments;

    public ParkingMateViewModel(Application application) {
        super(application);
        repository = new ParkingMateRepository(application);

        allAppointments = repository.getAllAppointments();
        allParkingSpaces = repository.getAllSpaces();
    }

    public LiveData<List<SpaceWithRestriction>> getAllParkingSpaces() { return allParkingSpaces; }

    public LiveData<List<Appointment>> getAllAppointments() {
        return allAppointments;
    }

    public void insert(SpaceWithRestriction parkingSpace) {
        repository.insertParkingSpace(parkingSpace);
    }

    public void insert(Appointment appointment) {
        repository.insert(appointment);
    }


}
