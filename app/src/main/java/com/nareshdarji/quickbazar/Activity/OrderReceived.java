package com.nareshdarji.quickbazar.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
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
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.RecyclerView.OrderReceivedAdapter;
import com.nareshdarji.quickbazar.Utility.NetworkConnectivity;
import com.nareshdarji.quickbazar.databinding.ActivityOrderReceivedBinding;
import com.nareshdarji.quickbazar.interfacePackage.onClick;

import java.util.ArrayList;
import java.util.List;

public class OrderReceived extends AppCompatActivity {

    ActivityOrderReceivedBinding binding;
    List<OrderBean> orderReceiverList;
    List<OrderBean> orderDeliveredList;
    List<OrderBean> adapterList;
    OrderReceivedAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    ColorStateList colorgrey;
    ColorStateList colorOrange;
    NetworkConnectivity networkConnectivity;
    String listType = "Received";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderReceivedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        colorgrey = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.grey));
        colorOrange = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary));

        orderReceiverList = new ArrayList<>();
        orderDeliveredList = new ArrayList<>();
        adapterList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        networkConnectivity = new NetworkConnectivity(this);
        adapter = new OrderReceivedAdapter(this, adapterList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.recyclerView.setAdapter(adapter);

        binding.btnPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterList.clear();
                adapterList.addAll(orderReceiverList);
                adapter.notifyDataSetChanged();
                binding.btnPending.setBackgroundTintList(colorOrange);
                binding.btnDelivered.setBackgroundTintList(colorgrey);
                if(adapterList.size()>0){
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.noDataAvailable.setVisibility(View.GONE);
                }else{
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.noDataAvailable.setVisibility(View.VISIBLE);
                }

                listType = "Received";

            }
        });
        binding.btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterList.clear();
                adapterList.addAll(orderDeliveredList);
                adapter.notifyDataSetChanged();
                binding.btnPending.setBackgroundTintList(colorgrey);
                binding.btnDelivered.setBackgroundTintList(colorOrange);
                if(adapterList.size()>0){
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.noDataAvailable.setVisibility(View.GONE);
                }else{
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.noDataAvailable.setVisibility(View.VISIBLE);
                }

                listType = "Delivered";
            }
        });

        getReceivedOrderList();
        getDeliveredOrderList();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getReceivedOrderList() {
        db.collection("Orders")
                .whereEqualTo("status", "Confirm")
                .whereEqualTo("dealerUid", mAuth.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        orderReceiverList.clear();
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                OrderBean orderBean = new OrderBean();
                                orderBean.setProductUid(document.getString("productUid"));
                                orderBean.setProductName(document.getString("productName"));
                                orderBean.setOrderUid(document.getString("orderUid"));
                                orderBean.setUserUid(document.getString("userUid"));
                                orderBean.setSize(Math.toIntExact((Long) document.get("size")));
                                orderBean.setColor(Math.toIntExact((Long) document.get("color")));
                                orderBean.setStatus(document.getString("status"));
                                orderBean.setQuantity(Math.toIntExact((Long) document.get("quantity")));
                                orderBean.setImgUri(document.getString("imgUri"));
                                orderBean.setPrice(document.getString("price"));
                                orderBean.setDealerUid(document.getString("dealerUid"));
                                orderBean.setUserName(document.getString("userName"));
                                orderBean.setMobile(document.getString("mobile"));
                                orderBean.setAddress(document.getString("address"));
                                orderReceiverList.add(orderBean);
                            }

                        }



                        if(listType.equals("Received")){
                            adapterList.clear();
                            adapterList.addAll(orderReceiverList);
                        }
                        adapter.notifyDataSetChanged();

                        if(adapterList.size()>0){
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            binding.noDataAvailable.setVisibility(View.GONE);
                        }else{
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.noDataAvailable.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void getDeliveredOrderList() {
        db.collection("Orders")
                .whereEqualTo("status", "Delivered")
                .whereEqualTo("dealerUid", mAuth.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        orderDeliveredList.clear();
                        if (!value.isEmpty()) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                OrderBean orderBean = new OrderBean();
                                orderBean.setProductUid(document.getString("productUid"));
                                orderBean.setProductName(document.getString("productName"));
                                orderBean.setOrderUid(document.getString("orderUid"));
                                orderBean.setUserUid(document.getString("userUid"));
                                orderBean.setSize(Math.toIntExact((Long) document.get("size")));
                                orderBean.setColor(Math.toIntExact((Long) document.get("color")));
                                orderBean.setStatus(document.getString("status"));
                                orderBean.setQuantity(Math.toIntExact((Long) document.get("quantity")));
                                orderBean.setImgUri(document.getString("imgUri"));
                                orderBean.setPrice(document.getString("price"));
                                orderBean.setDealerUid(document.getString("dealerUid"));
                                orderBean.setUserName(document.getString("userName"));
                                orderBean.setMobile(document.getString("mobile"));
                                orderBean.setAddress(document.getString("address"));
                                orderDeliveredList.add(orderBean);
                            }

                        }

                        if(listType.equals("Delivered")){
                            adapterList.clear();
                            adapterList.addAll(orderDeliveredList);
                        }

                        adapter.notifyDataSetChanged();
                        if(adapterList.size()>0){
                            binding.recyclerView.setVisibility(View.VISIBLE);
                            binding.noDataAvailable.setVisibility(View.GONE);
                        }else{
                            binding.recyclerView.setVisibility(View.GONE);
                            binding.noDataAvailable.setVisibility(View.VISIBLE);
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