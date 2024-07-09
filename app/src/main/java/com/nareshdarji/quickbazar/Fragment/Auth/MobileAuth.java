package com.nareshdarji.quickbazar.Fragment.Auth;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nareshdarji.quickbazar.Activity.HomeActivity;
import com.nareshdarji.quickbazar.Activity.SignUpOrLogin;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.databinding.FragmentAuthOptionBinding;

import java.util.concurrent.TimeUnit;

public class MobileAuth extends Fragment {


    FragmentAuthOptionBinding binding;
    Activity activity;
    DialogClass dialog;

    static final String TAG = "PhoneAuthActivity";
    FirebaseAuth mAuth;

    private FirebaseFirestore db;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentAuthOptionBinding.bind(inflater.inflate(R.layout.fragment_auth_option, container, false));
        activity = getActivity();
        FirebaseApp.initializeApp(getContext().getApplicationContext());
        dialog = new DialogClass(activity);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNo = binding.etMobileNo.getText().toString().trim();

                if(phoneNo.length()<10){

                    Toast.makeText(activity, "Please Enter Valid Mobile Number", Toast.LENGTH_LONG).show();

                }else{
                    dialog.progressShow("Verifying..");
                    phoneNo = "+91"+phoneNo;
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNo)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(activity)                 // (optional) Activity for callback binding
                                    // If no activity is passed, reCAPTCHA verification can not be used.
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }



            }
        });

        binding.btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp = binding.etOTP.getText().toString().trim();
                if(otp.length()<6){
                    Toast.makeText(activity, "Please Enter Valid OTP", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.progressShow("Verifying..");
                    verifyPhoneNumberWithCode(mVerificationId,otp);
                }


            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                dialog.progresshide();
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                mVerificationId = verificationId;
                mResendToken = token;

                binding.etMobileNo.setVisibility(View.GONE);
                binding.etOTP.setVisibility(View.VISIBLE);
                binding.btnSendOTP.setVisibility(View.GONE);
                binding.btnVerifyOTP.setVisibility(View.VISIBLE);
                dialog.progresshide();

            }
        };


        return binding.getRoot();
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    checkUserExists(mAuth.getUid());
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void checkUserExists(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(activity, HomeActivity.class));
                                }
                            }, 3000);
                        } else {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(activity, SignUpOrLogin.class));
                                }
                            }, 3000);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(activity, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }





}