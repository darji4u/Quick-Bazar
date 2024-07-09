package com.nareshdarji.quickbazar.Fragment.Home;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nareshdarji.quickbazar.Activity.OrderReceived;
import com.nareshdarji.quickbazar.Bean.OrderBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.RecyclerView.MyOrderHistoryAdapter;
import com.nareshdarji.quickbazar.RecyclerView.OrderReceivedAdapter;
import com.nareshdarji.quickbazar.databinding.FragmentOrderHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory extends Fragment {


    List<OrderBean> onTheWayList;
    List<OrderBean> deliveredList;
    List<OrderBean> adapterList;

    MyOrderHistoryAdapter adapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    String listType = "OnTheWay";
    ColorStateList colorgrey;
    ColorStateList colorOrange;

    FragmentOrderHistoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderHistoryBinding.bind(inflater.inflate(R.layout.fragment_order_history, container, false));
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        onTheWayList = new ArrayList<>();
        deliveredList = new ArrayList<>();
        adapterList = new ArrayList<>();

        colorgrey = ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.grey));
        colorOrange = ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.primary));

        adapter = new MyOrderHistoryAdapter(adapterList, getActivity());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false));
        binding.recyclerView.setAdapter(adapter);


        binding.btnOntheWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterList.clear();
                adapterList.addAll(onTheWayList);
                adapter.notifyDataSetChanged();
                binding.btnOntheWay.setBackgroundTintList(colorOrange);
                binding.btnDelivered.setBackgroundTintList(colorgrey);
                if(adapterList.size()>0){
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.noDataAvailable.setVisibility(View.GONE);
                }else{
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.noDataAvailable.setVisibility(View.VISIBLE);
                }

                listType = "OnTheWay";

            }
        });
        binding.btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterList.clear();
                adapterList.addAll(deliveredList);
                adapter.notifyDataSetChanged();
                binding.btnOntheWay.setBackgroundTintList(colorgrey);
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



        getOnTheWay();
        getReceivedOrder();


        return binding.getRoot();
    }

    private void getOnTheWay() {
        db.collection("Orders")
                .whereEqualTo("status", "Confirm")
                .whereEqualTo("userUid", mAuth.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        onTheWayList.clear();
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
                                onTheWayList.add(orderBean);
                            }

                        }



                        if(listType.equals("OnTheWay")){
                            adapterList.clear();
                            adapterList.addAll(onTheWayList);
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

    private void getReceivedOrder() {
        db.collection("Orders")
                .whereEqualTo("status", "Delivered")
                .whereEqualTo("userUid", mAuth.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        deliveredList.clear();
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
                                deliveredList.add(orderBean);
                            }

                        }

                        if(listType.equals("Delivered")){
                            adapterList.clear();
                            adapterList.addAll(deliveredList);
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

}