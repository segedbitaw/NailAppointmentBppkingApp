package com.example.frontendfinalproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.frontendfinalproject.R;

public class SplashFragment extends Fragment {


    public SplashFragment() {
        super(R.layout.fragment_splash_fragment);
    }
    public static SplashFragment newInstance(String param1, String param2) {
        SplashFragment fragment = new SplashFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        view.findViewById(R.id.btnLogin).setOnClickListener(v ->
                navController.navigate(R.id.action_splash_fragment_to_loginFragment));

        view.findViewById(R.id.btnRegister).setOnClickListener(v ->
                navController.navigate(R.id.action_splash_fragment_to_registerFragment));
    }
}