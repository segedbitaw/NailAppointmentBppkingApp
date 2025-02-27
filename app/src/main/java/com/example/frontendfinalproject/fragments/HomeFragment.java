package com.example.frontendfinalproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontendfinalproject.R;
import com.example.frontendfinalproject.adapters.BookingAdapter;
import com.example.frontendfinalproject.classes.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView recyclerViewA;
    private BookingAdapter adapterForBooked;
    private BookingAdapter adapterForAvailable;
    private List<Appointment> appointmentList;
    private List<Appointment> appointmentListAv;
    private DatabaseReference databaseReference;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_appointments);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewA = view.findViewById(R.id.recycler_view_booked);
        recyclerViewA.setLayoutManager(new LinearLayoutManager(getContext()));

        appointmentList = new ArrayList<>();
        adapterForBooked = new BookingAdapter(getContext(), appointmentList);

        appointmentListAv = new ArrayList<>();
        adapterForAvailable = new BookingAdapter(getContext(),appointmentListAv);

        recyclerView.setAdapter(adapterForBooked);
        recyclerViewA.setAdapter(adapterForAvailable);

        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            String userName = Objects.requireNonNull(user.getEmail()).split("@")[0];
            TextView tvWelcome = view.findViewById(R.id.tvWelcome);
            tvWelcome.setText("Welcome, " + (userName != null ? userName : "User"));
            loadAppointments(user.getUid());
        }
        setupSwipeToDelete();
        loadAvailableAppointments();
        return view;
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();

                Appointment appointment = appointmentList.get(position);
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("appointments");

                if (direction == ItemTouchHelper.RIGHT) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("status", "Available");
                    updates.put("userId", null);

                    database.child(appointment.getId()).updateChildren(updates)
                            .addOnSuccessListener(aVoid -> {

                                Toast.makeText(getContext(), "Appointment canceled", Toast.LENGTH_SHORT).show();

                                adapterForAvailable.notifyDataSetChanged();
                                adapterForBooked.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to cancel", Toast.LENGTH_SHORT).show());
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadAvailableAppointments() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentListAv.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Appointment appointment = dataSnapshot.getValue(Appointment.class);
                        if (appointment != null) {
                            // check if the status is available
                            String status = appointment.getStatus();
                            if ("Available".equals(status)) {
                                appointmentListAv.add(appointment);
                            }
                        }
                    }
                }
                adapterForAvailable.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadAppointments(String userId) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Appointment appointment = dataSnapshot.getValue(Appointment.class);
                        if (appointment != null) {
                            //  if userid is not null and matches the current userid
                            String appointmentUserId = appointment.getUserId();
                            if (appointmentUserId != null && appointmentUserId.equals(userId)) {
                                appointmentList.add(appointment);
                            }
                        }
                    }
                }
                adapterForBooked.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
