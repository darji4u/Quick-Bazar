package com.nareshdarji.quickbazar.Fragment.Auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nareshdarji.quickbazar.Activity.HomeActivity;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.CommonUtility;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.databinding.FragmentSignUpBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignUp extends Fragment {


    FragmentSignUpBinding binding;
    DialogClass dialog;

    String gender = null;
    String userType = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.bind(inflater.inflate(R.layout.fragment_sign_up, container, false));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        dialog = new DialogClass(getActivity());

        binding.btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnMale.setBackgroundResource(R.drawable.card_background);
                binding.btnFemale.setBackgroundResource(R.drawable.card_background_dark);
                binding.tvMale.setTextColor(getResources().getColor(R.color.black1));
                binding.tvFemale.setTextColor(getResources().getColor(R.color.grey));
                gender = "Male";
            }
        });

        binding.btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnMale.setBackgroundResource(R.drawable.card_background_dark);
                binding.btnFemale.setBackgroundResource(R.drawable.card_background);
                binding.tvMale.setTextColor(getResources().getColor(R.color.grey));
                binding.tvFemale.setTextColor(getResources().getColor(R.color.black1));
                gender = "Female";
            }
        });

        binding.btnDealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnDealer.setBackgroundResource(R.drawable.card_background);
                binding.btnClient.setBackgroundResource(R.drawable.card_background_dark);
                binding.tvDealer.setTextColor(getResources().getColor(R.color.black1));
                binding.tvClient.setTextColor(getResources().getColor(R.color.grey));
                binding.dealerContainer.setVisibility(View.VISIBLE);
                userType = "Dealer";
            }
        });

        binding.btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnDealer.setBackgroundResource(R.drawable.card_background_dark);
                binding.btnClient.setBackgroundResource(R.drawable.card_background);
                binding.tvDealer.setTextColor(getResources().getColor(R.color.grey));
                binding.tvClient.setTextColor(getResources().getColor(R.color.black1));
                binding.dealerContainer.setVisibility(View.GONE);
                userType = "Client";
            }
        });


        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertUserData();
            }
        });



        return binding.getRoot();
    }



    private void insertUserData() {

        String email = binding.etEmail.getText().toString().trim();
        String name = binding.etFullName.getText().toString().trim();
        String address = binding.etAddress.getText().toString().trim();
        String shopName = binding.etStoreName.getText().toString().trim();
        String shopAddress = binding.etStoreAddress.getText().toString().trim();



        if(!email.contains("@gmail.com")){
            Toast.makeText(getActivity(), "Please Enter Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(name.isEmpty()){
            Toast.makeText(getActivity(), "Invalid Name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(address.isEmpty()){
            Toast.makeText(getActivity(), "Invalid Address", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(gender==null){
            Toast.makeText(getActivity(), "Please Select Gender", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(userType==null){
            Toast.makeText(getActivity(), "Please Select User Type", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(userType!=null && userType.equals("Dealer")){
            if(shopName.isEmpty()){
                Toast.makeText(getActivity(), "Please Enter Shop Name", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(shopAddress.isEmpty()){
                Toast.makeText(getActivity(), "Please Enter Shop Address", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        dialog.progressShow("Please Wait...");
        FirebaseUser user = mAuth.getCurrentUser();

        String shopID = null;

        if(userType.equals("Dealer")){

            shopID = CommonUtility.generateUID();
            Map<String, Object> storeDetail = new HashMap<>();
            storeDetail.put("uid", shopID);
            storeDetail.put("dealer", user.getUid());
            storeDetail.put("shopName", shopName);
            storeDetail.put("shopAddress", shopAddress);
            db.collection("shops").document(shopID)
                    .set(storeDetail)
                    .addOnSuccessListener(bVoid -> {

                    })
                    .addOnFailureListener(e -> {

                    });

        }


        List<String> addressList = new ArrayList<>();
        addressList.add(address);

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("name", name);
        userData.put("email", email);
        userData.put("address", addressList);
        userData.put("gender", gender);
        userData.put("userType", userType);
        userData.put("mobile", mAuth.getCurrentUser().getPhoneNumber());
        userData.put("auth", !userType.equals("Dealer"));
        userData.put("shopUid", shopID==null?"":shopID);

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Register Successfully", Toast.LENGTH_SHORT).show();
                    dialog.progresshide();
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                })
                .addOnFailureListener(e -> {
                    dialog.progresshide();
                    Toast.makeText(getActivity(), "Failed to add user data to Firestore", Toast.LENGTH_SHORT).show();
                });
    }

}