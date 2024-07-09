package com.nareshdarji.quickbazar.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nareshdarji.quickbazar.Bean.OrderBean;
import com.nareshdarji.quickbazar.Bean.UserBean;
import com.nareshdarji.quickbazar.RecyclerView.DealerRequestAdapter;
import com.nareshdarji.quickbazar.Utility.NetworkConnectivity;
import com.nareshdarji.quickbazar.databinding.ActivityDealerRequestBinding;

import java.util.ArrayList;
import java.util.List;

public class DealerRequest extends AppCompatActivity {


    ActivityDealerRequestBinding binding;

    List<UserBean> adapterList;
    DealerRequestAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    NetworkConnectivity networkConnectivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDealerRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        networkConnectivity = new NetworkConnectivity(this);
        adapterList = new ArrayList<>();
        adapter = new DealerRequestAdapter(adapterList,this);
        binding.rvRequest.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false));
        binding.rvRequest.setAdapter(adapter);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getDealerList();
    }

    private void getDealerList() {

        db.collection("users")
                .whereEqualTo("userType", "Dealer")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        adapterList.clear();
                        if (!value.isEmpty()) {
                            binding.noDataAvailable.setVisibility(View.GONE);
                            binding.rvRequest.setVisibility(View.VISIBLE);
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                UserBean userBean = new UserBean();
                                userBean.setUserUid(documentSnapshot.getString("uid"));
                                userBean.setName(documentSnapshot.getString("name"));
                                userBean.setGender(documentSnapshot.getString("gender"));
                                userBean.setAuth(documentSnapshot.getBoolean("auth"));
                                userBean.setMobile(documentSnapshot.getString("mobile"));
                                adapterList.add(userBean);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            binding.noDataAvailable.setVisibility(View.VISIBLE);
                            binding.rvRequest.setVisibility(View.GONE);
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