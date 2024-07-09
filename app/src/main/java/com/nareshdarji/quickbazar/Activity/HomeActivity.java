package com.nareshdarji.quickbazar.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.nareshdarji.quickbazar.Bean.UserBean;
import com.nareshdarji.quickbazar.Fragment.Home.Cart;
import com.nareshdarji.quickbazar.Fragment.Home.OrderHistory;
import com.nareshdarji.quickbazar.Fragment.Home.Products;
import com.nareshdarji.quickbazar.Fragment.Home.Profile;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.NetworkConnectivity;
import com.nareshdarji.quickbazar.databinding.ActivityHomeBinding;

import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    ActivityHomeBinding binding;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;

    NetworkConnectivity networkConnectivity;
    public static UserBean CurrentUserDetail;
    MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        networkConnectivity = new NetworkConnectivity(this);
        getUserDetails();





        @SuppressLint("ResourceType") ColorStateList colorStateList = getResources().getColorStateList(R.xml.nav_selecter);
        binding.bottomNavigation.setItemIconTintList(colorStateList);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.nb_home:
                        selectedFragment = new Products();
                        break;
                    case R.id.nb_cart:
                        selectedFragment = new Cart();
                        break;
                    case R.id.nb_orders:
                        selectedFragment = new OrderHistory();
                        break;
                    case R.id.nb_profile:
                        selectedFragment = new Profile();
                        break;
                }
                if (selectedFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.flHome, selectedFragment);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });
        binding.bottomNavigation.setSelectedItemId(R.id.nb_home);

        binding.icNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),OrderReceived.class));
            }
        });


    }

    private void getUserDetails() {

        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {
                    CurrentUserDetail = new UserBean();
                    CurrentUserDetail.setName(documentSnapshot.getString("name"));
                    CurrentUserDetail.setEmail(documentSnapshot.getString("email"));
                    CurrentUserDetail.setAddress((List<String>) documentSnapshot.get("address"));
                    CurrentUserDetail.setGender(documentSnapshot.getString("gender"));
                    CurrentUserDetail.setUserType(documentSnapshot.getString("userType"));
                    CurrentUserDetail.setShopUid(documentSnapshot.getString("shopUid"));
                    CurrentUserDetail.setAuth(documentSnapshot.getBoolean("auth"));
                    CurrentUserDetail.setMobile(documentSnapshot.getString("mobile"));
                    if(CurrentUserDetail.getUserType().equals("Dealer")){

                        if(CurrentUserDetail.getUserType().equals("Dealer")&& CurrentUserDetail.getAuth()){
                            binding.icNotification.setVisibility(View.VISIBLE);
                        }else{
                            binding.icNotification.setVisibility(View.GONE);
                        }

                        DocumentReference docRef = db.collection("shops").document(CurrentUserDetail.getShopUid());

                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    CurrentUserDetail.setShopName(documentSnapshot.getString("shopName"));
                                    CurrentUserDetail.setShopAddress(documentSnapshot.getString("shopAddress"));
                                } else {
                                    mAuth.signOut();
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mAuth.signOut();
                                finish();
                            }
                        });

                    }else{
                        CurrentUserDetail.setShopName("");
                        CurrentUserDetail.setShopAddress("");
                        binding.icNotification.setVisibility(View.GONE);
                    }
                    binding.tvCurrUserName.setText("Hi, "+CurrentUserDetail.getName().split("\\s+")[0]);
                } else {
                    mAuth.signOut();
                    finish();
                }
            }
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