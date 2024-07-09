package com.nareshdarji.quickbazar.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import com.nareshdarji.quickbazar.Bean.ProductBean;
import com.nareshdarji.quickbazar.RecyclerView.MyProductAdapter;
import com.nareshdarji.quickbazar.Utility.NetworkConnectivity;
import com.nareshdarji.quickbazar.databinding.ActivityMyProductsBinding;

import java.util.ArrayList;
import java.util.List;

public class MyProducts extends AppCompatActivity {


    ActivityMyProductsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    List<ProductBean> productList;
    MyProductAdapter adapter;

    NetworkConnectivity networkConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        networkConnectivity = new NetworkConnectivity(this);
        productList = new ArrayList<>();

        setView();
        adapter = new MyProductAdapter(this,productList);
        binding.rvProducts.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rvProducts.setAdapter(adapter);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setView() {

        db.collection("Products")
                .whereEqualTo("dealerUid", mAuth.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        productList.clear();
                        if (!value.isEmpty()) {
                            for(DocumentSnapshot snapshot : value.getDocuments()){
                                ProductBean bean = new ProductBean();
                                bean.setName(snapshot.getString("name"));
                                bean.setDescription(snapshot.getString("description"));
                                bean.setPrice(snapshot.getString("price"));
                                bean.setCategory(snapshot.getString("category"));
                                bean.setSizes((List<Integer>) snapshot.get("size"));
                                bean.setColors((List<Integer>) snapshot.get("colors"));
                                bean.setProductUid(snapshot.getString("productUid"));
                                bean.setDealerUid(snapshot.getString("dealerUid"));
                                bean.setImgUrl(snapshot.getString("imgUri"));
                                bean.setAvailability(snapshot.getString("availability"));

                                productList.add(bean);
                            }
                            adapter.notifyDataSetChanged();
                            binding.noDataAvailable.setVisibility(View.GONE);
                            binding.rvProducts.setVisibility(View.VISIBLE);

                        } else {
                            binding.noDataAvailable.setVisibility(View.VISIBLE);
                            binding.rvProducts.setVisibility(View.GONE);
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