package com.example.frontendfinalproject.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.frontendfinalproject.R;
import com.example.frontendfinalproject.activities.MainActivity;



public class LoginFragment extends Fragment {

    public LoginFragment() {}
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button button1 = view.findViewById(R.id.Login_btn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                if(mainActivity!=null){
                    mainActivity.login(v);
                }
            }
        });

        return view;
    }
}