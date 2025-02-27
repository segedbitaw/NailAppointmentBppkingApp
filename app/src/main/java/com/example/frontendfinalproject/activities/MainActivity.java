package com.example.frontendfinalproject.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.frontendfinalproject.R;
import com.example.frontendfinalproject.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;




public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main);
        NavController navController = navHostFragment.getNavController();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
    }

public void signUp() {
    String email = ((EditText) findViewById(R.id.etEmail)).getText().toString().trim();
    String password = ((EditText) findViewById(R.id.etPassword)).getText().toString().trim();
    String con_pass = ((EditText) findViewById(R.id.etConfirmPassword)).getText().toString().trim();
    if (email.isEmpty() || password.isEmpty()) {
        Toast.makeText(MainActivity.this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
        return;
    }
    if(!con_pass.equals(password)){
        Toast.makeText(MainActivity.this, "passwords are not the same", Toast.LENGTH_SHORT).show();
    }

    mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    addUser();
                    Navigation.findNavController(this, R.id.main).navigate(R.id.action_registerFragment_to_loginFragment);

                } else {
                    Toast.makeText(MainActivity.this, "Registration failed: ", Toast.LENGTH_LONG).show();
                }
            });
}


    public void login(View view) {
        String email = ((EditText) findViewById(R.id.etEmailAdrs)).getText().toString();
        String password = ((EditText) findViewById(R.id.etPasswordLgn)).getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.getEmail().equals("admin@gmail.com")) {
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_adminDashboardFragment);
                        } else {
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_homeFragment);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addUser(){
        String phone = ((EditText)findViewById(R.id.etPhone)).getText().toString();
        String name = ((EditText)findViewById(R.id.etUserName)).getText().toString();
        String email = ((EditText)findViewById(R.id.etEmail)).getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid);

        User user = new User(name,email,phone,uid);
        myRef.setValue(user).addOnSuccessListener(aVoid ->
                Toast.makeText(this, "User added ", Toast.LENGTH_SHORT).show()
        )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show()
                );
    }


}