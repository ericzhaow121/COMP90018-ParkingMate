package com.comp900019.MelbParking;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ParkingMateRepository {

    private AppointmentDao appointmentDao;
    private ParkingSpaceDAO parkingSpaceDAO;



    private LiveData<List<Appointment>> allAppointments;
    private LiveData<List<SpaceWithRestriction>> allSpaces;

    ParkingMateRepository(Application application) {
        ParkingMateDatabase db = ParkingMateDatabase.getDataBase(application);

        appointmentDao = db.appointmentDao();
        allAppointments = appointmentDao.selectAll();


        parkingSpaceDAO = db.parkingSpaceDAO();
        allSpaces = parkingSpaceDAO.getAllSpaces();

    }

    LiveData<List<Appointment>> getAllAppointments() {
        return allAppointments;
    }

    LiveData<List<SpaceWithRestriction>> getAllSpaces() { return allSpaces; }

    void insert(Appointment appointment) {
        new InsertAppointmentAsyncTask(appointmentDao).execute(appointment);
    }

    public void insertParkingSpace(SpaceWithRestriction ps) {
        new InsertParkingSpaceAsyncTask(parkingSpaceDAO).execute(ps);
    }

    private static class InsertParkingSpaceAsyncTask extends AsyncTask<SpaceWithRestriction, Void, Void> {
        private ParkingSpaceDAO psDao;

        InsertParkingSpaceAsyncTask(ParkingSpaceDAO dao) { psDao = dao; }

        @Override
        protected Void doInBackground(SpaceWithRestriction... parkingSpaces) {
            psDao.insertParkingSpaceWithRestriction(parkingSpaces[0]);
            return null;
        }
    }

    private static class InsertAppointmentAsyncTask extends AsyncTask<Appointment, Void, Void> {

        private AppointmentDao asyncTaskDao;

        InsertAppointmentAsyncTask(AppointmentDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(@NonNull final Appointment... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }


}
