package com.nareshdarji.quickbazar.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.nareshdarji.quickbazar.Fragment.Auth.MobileAuth;
import com.nareshdarji.quickbazar.Fragment.Auth.SignUp;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.NetworkConnectivity;
import com.nareshdarji.quickbazar.databinding.ActivitySignUpOrLoginBinding;

public class SignUpOrLogin extends AppCompatActivity {


    ActivitySignUpOrLoginBinding binding;
    FirebaseAuth mAuth;
    NetworkConnectivity networkConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_sign_up_or_login);

        mAuth = FirebaseAuth.getInstance();
        networkConnectivity = new NetworkConnectivity(this);

        if(mAuth.getCurrentUser()==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.authFramLayout, MobileAuth.class,null).commit();
        }else{
            getSupportFragmentManager().beginTransaction().replace(R.id.authFramLayout, SignUp.class,null).commit();
        }


    }

    @Override
    protected void onResume() {
        registerReceiver(networkConnectivity, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkConnectivity);
        super.onPause();
    }
}