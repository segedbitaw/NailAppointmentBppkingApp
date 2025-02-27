package com.example.frontendfinalproject.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontendfinalproject.R;
import com.example.frontendfinalproject.classes.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private Context context;
    private List<Appointment> appointmentList;
    private DatabaseReference databaseReference;

    public BookingAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("appointments");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);

        holder.textDate.setText("Date: " + appointment.getDate());
        holder.textTime.setText("Time: " + appointment.getTime());


        holder.buttonBook.setOnClickListener(v -> {
            if (appointment.getId() == null || appointment.getId().isEmpty()) {
                return;
            }

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(context, "please log in", Toast.LENGTH_SHORT).show();
                return;
            }
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

            userRef.get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    String userIdFromDatabase = dataSnapshot.child("id").getValue(String.class);
                    if (userIdFromDatabase == null || userIdFromDatabase.isEmpty()) {
                        Toast.makeText(context, "Error: User ID is missing in database!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    databaseReference.child(appointment.getId()).child("status").setValue("Booked")
                            .addOnSuccessListener(aVoid -> {
                                databaseReference.child(appointment.getId()).child("userId").setValue(userIdFromDatabase)
                                        .addOnSuccessListener(aVoid2 -> {
                                            appointment.setStatus("Booked");
                                            appointment.setUserId(userIdFromDatabase);
                                            notifyItemChanged(holder.getAdapterPosition());
                                            Toast.makeText(context, "Appointment booked", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(context, "Failed to set user ID: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                        );
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Booking Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );

                } else {
                    Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(context, "Failed" , Toast.LENGTH_SHORT).show()
            );
        });

        if ("Booked".equals(appointment.getStatus())) {
            holder.buttonBook.setEnabled(false);
            holder.buttonBook.setText("Booked");
        } else {
            holder.buttonBook.setEnabled(true);
            holder.buttonBook.setText("Book");
        }
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textTime;
        Button buttonBook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textTime = itemView.findViewById(R.id.text_time);
            buttonBook = itemView.findViewById(R.id.button_book);
        }
    }
}

