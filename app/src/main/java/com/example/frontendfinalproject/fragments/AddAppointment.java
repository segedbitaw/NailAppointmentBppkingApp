package com.example.frontendfinalproject.fragments;

import static android.text.TextUtils.isEmpty;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.frontendfinalproject.R;
import com.example.frontendfinalproject.classes.Appointment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddAppointment extends Fragment {

    private EditText editTextDate, editTextTime;
    private Button buttonAddAppointment,buttonGoToDash;
    private DatabaseReference databaseReference;

    public AddAppointment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_appointment, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");

        editTextDate = view.findViewById(R.id.editTextDate);
        editTextTime = view.findViewById(R.id.editTextTime);
        buttonAddAppointment = view.findViewById(R.id.buttonAddAppointment);
        buttonGoToDash = view.findViewById(R.id.GoToDash);

        buttonAddAppointment.setOnClickListener(v -> addAppointmentToDatabase());
        buttonGoToDash.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_addAppointment_to_adminDashboardFragment));

        return view;
    }

    private void addAppointmentToDatabase() {
        String date = editTextDate.getText().toString();
        String time = editTextTime.getText().toString();

        if (isEmpty(date)) {
            Toast.makeText(getActivity(), "Enter Date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEmpty(time)) {
            Toast.makeText(getActivity(), "Enter Time", Toast.LENGTH_SHORT).show();
            return;
        }


        String appointmentId = databaseReference.push().getKey();

        Appointment appointment = new Appointment(appointmentId, date, time, "Available",null);

        if (appointmentId != null) {
            databaseReference.child(appointmentId).setValue(appointment)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Appointment Added", Toast.LENGTH_SHORT).show();
                        editTextDate.setText("");
                        editTextTime.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to add appointment", Toast.LENGTH_SHORT).show());
        }
    }
}
