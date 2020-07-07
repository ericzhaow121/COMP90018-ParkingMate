package com.comp900019.MelbParking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;

        private ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.appointment_card_view);
        }
    }

    public interface OnAppointmentSelectedListener {
        public void onAppointmentSelected(String location);
    }

    private final LayoutInflater inflater;
    private List<Appointment> appointments;
    private OnAppointmentSelectedListener callback;
    private Toast toast;

    AppointmentAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        if (context instanceof OnAppointmentSelectedListener) {
            callback = (OnAppointmentSelectedListener) context;
            toast = null;
        } else {
            callback = null;
            toast = Toast.makeText(context.getApplicationContext(),
                "No listener found, unable to locate the event.", Toast.LENGTH_SHORT);
        }
    }

    void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.appointment_recycler_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (appointments != null && position < appointments.size()) {
            final Appointment appointment = appointments.get(position);
            CardView cardView = holder.cardView;

            TextView nameView = cardView.findViewById(R.id.appointment_name);
            nameView.setText(appointment.getName());

            TextView timeView = cardView.findViewById(R.id.appointment_time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(appointment.getTime());
            timeView.setText(String.format(Locale.getDefault(), "%tH:%tM", calendar, calendar));

            TextView locView = cardView.findViewById(R.id.appointment_location);
            locView.setText(appointment.getLocation());

            // TODO: Add on-click listeners to the search button of each event
            // uses a broadcast intent to send to MainActivity
            // receive it there, change the tab to parks
            // send intent to Parks
            // if it exists, look up a place, then display the closest park.

            ImageButton button = cardView.findViewById(R.id.search_location_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback == null) {
                        toast.show();
                    } else {
                        callback.onAppointmentSelected(appointment.getLocation());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (appointments == null) {
            return 0;
        } else {
            return appointments.size();
        }
    }

    public Appointment getAppointment(int position) {
        return appointments.get(position);
    }
}
