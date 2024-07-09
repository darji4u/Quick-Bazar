package com.nareshdarji.quickbazar.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.CommonUtility;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.Utility.NetworkConnectivity;
import com.nareshdarji.quickbazar.databinding.ActivityEditProfileBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding binding;

    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    DialogClass dialogClass;

    String userType = "";
    NetworkConnectivity networkConnectivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dialogClass = new DialogClass(this);
        userType = HomeActivity.CurrentUserDetail.getUserType();
        networkConnectivity = new NetworkConnectivity(this);
        setView();

        binding.btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnMale.setBackgroundResource(R.drawable.card_background_blue);
                binding.btnFemale.setBackgroundResource(R.drawable.card_background);
                HomeActivity.CurrentUserDetail.setGender("Male");
            }
        });

        binding.btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnMale.setBackgroundResource(R.drawable.card_background);
                binding.btnFemale.setBackgroundResource(R.drawable.card_background_blue);
                HomeActivity.CurrentUserDetail.setGender("Female");
            }
        });


        binding.btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnDealer.setBackgroundResource(R.drawable.card_background);
                binding.btnClient.setBackgroundResource(R.drawable.card_background_blue);
                binding.tvDealer.setTextColor(getResources().getColor(R.color.black1));
                binding.tvClient.setTextColor(getResources().getColor(R.color.grey));
                binding.dealerContainer.setVisibility(View.GONE);
                binding.etStoreName.setText(HomeActivity.CurrentUserDetail.getShopName());
                binding.etStoreAddress.setText(HomeActivity.CurrentUserDetail.getShopAddress());
                userType = "Client";
            }
        });

        binding.btnDealer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.btnDealer.setBackgroundResource(R.drawable.card_background_blue);
                binding.btnClient.setBackgroundResource(R.drawable.card_background);
                binding.tvDealer.setTextColor(getResources().getColor(R.color.grey));
                binding.tvClient.setTextColor(getResources().getColor(R.color.black1));
                binding.dealerContainer.setVisibility(View.VISIBLE);
                binding.etStoreName.setText(HomeActivity.CurrentUserDetail.getShopName());
                binding.etStoreAddress.setText(HomeActivity.CurrentUserDetail.getShopAddress());
                userType = "Dealer";
            }
        });


        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserData();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }

    protected void setView() {

        binding.etEmail.setText(HomeActivity.CurrentUserDetail.getEmail());
        binding.etFullName.setText(HomeActivity.CurrentUserDetail.getName());

        if (HomeActivity.CurrentUserDetail.getGender().equals("Male")) {
            binding.btnMale.setBackgroundResource(R.drawable.card_background_blue);
            binding.btnFemale.setBackgroundResource(R.drawable.card_background);
        } else {
            binding.btnMale.setBackgroundResource(R.drawable.card_background);
            binding.btnFemale.setBackgroundResource(R.drawable.card_background_blue);
        }


        if (HomeActivity.CurrentUserDetail.getUserType().equals("Dealer") && HomeActivity.CurrentUserDetail.isAuth()) {
            binding.btnDealer.setVisibility(View.GONE);
            binding.btnClient.setVisibility(View.GONE);
            binding.dealerContainer.setVisibility(View.VISIBLE);
            binding.etStoreName.setText(HomeActivity.CurrentUserDetail.getShopName());
            binding.etStoreAddress.setText(HomeActivity.CurrentUserDetail.getShopAddress());

        } else if (HomeActivity.CurrentUserDetail.getUserType().equals("Dealer") && !HomeActivity.CurrentUserDetail.isAuth()) {
            binding.btnDealer.setVisibility(View.GONE);
            binding.btnClient.setVisibility(View.GONE);
            binding.btnDealer.setBackgroundResource(R.drawable.card_background);
            binding.btnClient.setBackgroundResource(R.drawable.card_background_blue);
            binding.etStoreName.setText(HomeActivity.CurrentUserDetail.getShopName());
            binding.etStoreAddress.setText(HomeActivity.CurrentUserDetail.getShopAddress());
        } else {
            binding.btnDealer.setVisibility(View.VISIBLE);
            binding.btnClient.setVisibility(View.VISIBLE);
            binding.dealerContainer.setVisibility(View.GONE);
            binding.btnClient.setBackgroundResource(R.drawable.card_background_blue);
            binding.btnDealer.setBackgroundResource(R.drawable.card_background);
        }

    }

    private void updateUserData() {

        DocumentReference docRef = db.collection("users").document(mAuth.getUid());

        String email = binding.etEmail.getText().toString().trim();
        String name = binding.etFullName.getText().toString().trim();
        String shopName = binding.etStoreName.getText().toString().trim();
        String shopAddress = binding.etStoreAddress.getText().toString().trim();



        if(!email.contains("@gmail.com")){
            Toast.makeText(this, "Please Enter Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(name.isEmpty()){
            Toast.makeText(this, "Invalid Name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(HomeActivity.CurrentUserDetail.getGender()==null){
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return;
        }

        else if(userType!=null && userType.equals("Dealer")){
            if(shopName.isEmpty()){
                Toast.makeText(this, "Please Enter Shop Name", Toast.LENGTH_SHORT).show();
                return;
            }
            else if(shopAddress.isEmpty()){
                Toast.makeText(this, "Please Enter Shop Address", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        dialogClass.progressShow("Uploading Product Details..");


        String shopID = null;

        if(userType.equals("Dealer") && HomeActivity.CurrentUserDetail.getShopUid().equals("")){

            shopID = CommonUtility.generateUID();
            Map<String, Object> storeDetail = new HashMap<>();
            storeDetail.put("uid", shopID);
            storeDetail.put("dealer", mAuth.getUid());
            storeDetail.put("shopName", shopName);
            storeDetail.put("shopAddress", shopAddress);
            docRef = db.collection("shops").document(shopID);
            docRef.set(storeDetail)
                    .addOnSuccessListener(bVoid -> {

                    })
                    .addOnFailureListener(e -> {

                    });

        }
        else if(HomeActivity.CurrentUserDetail.getUserType().equals("Dealer") && !HomeActivity.CurrentUserDetail.getShopUid().equals("")) {

            shopID = HomeActivity.CurrentUserDetail.getShopUid();
            Map<String, Object> storeDetail = new HashMap<>();
            storeDetail.put("uid", shopID);
            storeDetail.put("dealer", mAuth.getUid());
            storeDetail.put("shopName", shopName);
            storeDetail.put("shopAddress", shopAddress);
            docRef = db.collection("shops").document(shopID);
            docRef
                    .update(storeDetail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {

                            }else {
                                Toast.makeText(getApplicationContext(),"Something Went wront",Toast.LENGTH_LONG).show();
                            }
                        }
                    });


        }


        List<String> addressList = HomeActivity.CurrentUserDetail.getAddress();

        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", mAuth.getUid());
        userData.put("name", name);
        userData.put("email", email);
        userData.put("address", addressList);
        userData.put("gender", HomeActivity.CurrentUserDetail.getGender());
        userData.put("userType", userType);

        userData.put("auth", userType.equals("Dealer")?false:true);
        userData.put("shopUid", shopID==null?"":shopID);

        db.collection("users").document(mAuth.getUid())
                .update(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                    dialogClass.progresshide();
                    finish();
                })
                .addOnFailureListener(e -> {
                    dialogClass.progresshide();
                    Toast.makeText(getApplicationContext(), "Failed to add user data to Firestore", Toast.LENGTH_SHORT).show();
                });

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