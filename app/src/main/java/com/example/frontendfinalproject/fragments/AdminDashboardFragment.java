package com.example.frontendfinalproject.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.frontendfinalproject.R;
import com.example.frontendfinalproject.adapters.AppointmentAdapter;
import com.example.frontendfinalproject.classes.Appointment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AdminDashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;
    private DatabaseReference databaseReference;


    public AdminDashboardFragment() {

    }

    public static AdminDashboardFragment newInstance(String param1, String param2) {
        AdminDashboardFragment fragment = new AdminDashboardFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        Button AddBtn = view.findViewById(R.id.btnAddAppointment);
        AddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_adminDashboardFragment_to_addAppointment);
            }
        });

        recyclerView = view.findViewById(R.id.appointmentsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(getContext(), appointmentList);
        recyclerView.setAdapter(adapter);


        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");


        loadAppointments();
        SwipeToDeleteOrUpdate();

        return view;
    }

    private void loadAppointments() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void SwipeToDeleteOrUpdate() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Appointment appointment = appointmentList.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    showMoveOrDeleteDialog(appointment, position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    updateAppointment(appointment, position);
                    recyclerView.getAdapter().notifyItemChanged(position); // Restore item visually
                }
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showMoveOrDeleteDialog(Appointment appointment, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Appointment Options")
                .setMessage("Do you want to free it or delete it?")
                .setPositiveButton("Free appointment", (dialog, which) -> {
                    makeAppointmentAvailable(appointment, position);
                })
                .setNegativeButton("Delete", (dialog, which) -> {
                    showDeleteConfirmationDialog(appointment, position);
                })
                .setNeutralButton("Cancel", (dialog, which) -> {
                    recyclerView.getAdapter().notifyItemChanged(position);
                })
                .setCancelable(false)
                .show();
    }

    private void makeAppointmentAvailable(Appointment appointment, int position) {
        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("appointments");

        // to make appointment available and remove it from specific user
        appointment.setStatus("Available");
        appointment.setUserId(null);

        appointmentRef.child(appointment.getId()).setValue(appointment)
                .addOnSuccessListener(aVoid -> {
                    recyclerView.getAdapter().notifyItemChanged(position);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                );
    }

    private void showDeleteConfirmationDialog(Appointment appointment, int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Appointment")
                .setMessage("Are You Sure?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("appointments");

                    appointmentRef.child(appointment.getId()).removeValue().addOnSuccessListener(aVoid -> {

                        appointmentList.remove(position);
                        recyclerView.getAdapter().notifyItemRemoved(position);

                    }).addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show()
                    );
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    recyclerView.getAdapter().notifyItemChanged(position);
                })
                .setCancelable(false)
                .show();
    }

    private void updateAppointment(Appointment appointment,int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Appointment");

        final View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_appointment, null);
        builder.setView(dialogView);

        EditText etDate = dialogView.findViewById(R.id.etUpdateDate);
        EditText etTime = dialogView.findViewById(R.id.etUpdateTime);


        etDate.setText(appointment.getDate());
        etTime.setText(appointment.getTime());


        builder.setPositiveButton("Update", (dialog, which) -> {

            String newDate = etDate.getText().toString().trim();
            String newTime = etTime.getText().toString().trim();
            if (newDate.isEmpty() || newTime.isEmpty()) {
                Toast.makeText(getContext(), "Handel empty fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("appointments").child(appointment.getId());
            appointmentRef.child("date").setValue(newDate);
            appointmentRef.child("time").setValue(newTime)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                        } else {
                            Toast.makeText(getContext(), "update failed", Toast.LENGTH_SHORT).show();
                        }
                    });
            adapter.notifyItemChanged(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            adapter.notifyItemChanged(position);
            dialog.dismiss();
        });
        builder.create().show();
    }

}