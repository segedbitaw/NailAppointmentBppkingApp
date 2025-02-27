package com.example.frontendfinalproject.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontendfinalproject.R;
import com.example.frontendfinalproject.classes.Appointment;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.appointmentTime.setText("Time: " +appointment.getTime());
        holder.appointmentDate.setText("Date: " +appointment.getDate());
        holder.appointmentStatus.setText("Status: "+appointment.getStatus());

    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {

        TextView appointmentTime, appointmentDate,appointmentStatus;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            appointmentTime = itemView.findViewById(R.id.text_time);
            appointmentDate = itemView.findViewById(R.id.text_date);
            appointmentStatus = itemView.findViewById(R.id.text_status);
        }
    }
}

