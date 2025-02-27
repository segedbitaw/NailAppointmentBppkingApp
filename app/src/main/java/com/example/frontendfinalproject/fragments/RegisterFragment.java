package com.example.frontendfinalproject.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.frontendfinalproject.R;
import com.example.frontendfinalproject.activities.MainActivity;



public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        Button button2 = view.findViewById(R.id.Register_btn);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if(mainActivity!=null){
                    mainActivity.signUp();
                }
            }
        });
        return  view;
    }
}