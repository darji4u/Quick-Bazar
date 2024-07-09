package com.nareshdarji.quickbazar.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nareshdarji.quickbazar.Bean.OrderBean;
import com.nareshdarji.quickbazar.Bean.ProductBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.CommonUtility;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.Utility.NetworkConnectivity;
import com.nareshdarji.quickbazar.databinding.ActivityProductDetailBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductDetail extends AppCompatActivity {

    ActivityProductDetailBinding binding;
    ColorStateList colorgrey;
    ColorStateList colorOrange;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    ProductBean productBean;
    OrderBean orderBean;
    Integer selectedColor;
    Integer selectedSize;
    boolean isExistInCart = false;
    OrderBean existCartBean = null;
    DialogClass dialogClass;

    NetworkConnectivity networkConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dialogClass = new DialogClass(this);
        networkConnectivity = new NetworkConnectivity(this);
        setView();

        colorgrey = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.greyLight));
        colorOrange = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary));


        binding.btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unCheckAllColors();
                    binding.btnWhite.setBackgroundResource(R.drawable.card_background_blue);
                    selectedColor = 1;
            }
        });
        binding.btnBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unCheckAllColors();
                    binding.btnBlack.setBackgroundResource(R.drawable.card_background_blue);
                    selectedColor = 2;
            }
        });

        binding.btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unCheckAllColors();
                    binding.btnBlue.setBackgroundResource(R.drawable.card_background_blue);
                    selectedColor = 3;
            }
        });

        binding.btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unCheckAllColors();
                    binding.btnRed.setBackgroundResource(R.drawable.card_background_blue);
                    selectedColor = 4;
            }
        });

        binding.btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unCheckAllColors();
                    binding.btnGreen.setBackgroundResource(R.drawable.card_background_blue);
                    selectedColor = 5;
            }
        });




        binding.proS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaultViewState();
                binding.proS.setBackgroundTintList(colorOrange);
                binding.proS.setTextColor(getResources().getColor(R.color.white));
                selectedSize = 1;
            }
        });
        binding.proM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaultViewState();
                binding.proM.setBackgroundTintList(colorOrange);
                binding.proM.setTextColor(getResources().getColor(R.color.white));
                selectedSize = 2;
            }
        });
        binding.proL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaultViewState();
                binding.proL.setBackgroundTintList(colorOrange);
                binding.proL.setTextColor(getResources().getColor(R.color.white));
                selectedSize = 3;
            }
        });
        binding.proXL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                setDefaultViewState();
                binding.proXL.setBackgroundTintList(colorOrange);
                binding.proXL.setTextColor(getResources().getColor(R.color.white));
                selectedSize = 4;
            }
        });

        binding.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qty = binding.etQuantity.getText().toString().trim();
                if(qty.equals("")){
                    binding.etQuantity.setText("1");
                }else{
                    int val = Integer.parseInt(qty);
                    if(val<99){
                        binding.etQuantity.setText(String.valueOf(val+1));
                    }
                }
            }
        });

        binding.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qty = binding.etQuantity.getText().toString().trim();
                if(qty.equals("")){
                    binding.etQuantity.setText("1");
                }else{
                    int val = Integer.parseInt(qty);
                    if(val>1){
                        binding.etQuantity.setText(String.valueOf(val-1));
                    }
                }
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderBean==null){

                    if(productBean.getSizes().size()>0 && selectedSize==null){
                        Toast.makeText(ProductDetail.this, "Please Select Size", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(productBean.getColors().size()>0 && selectedColor==null){
                        Toast.makeText(ProductDetail.this, "Please Select Color", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String quantity = binding.etQuantity.getText().toString();
                    if(quantity.equals("")){
                        Toast.makeText(getApplicationContext(), "Please enter quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(!quantity.equals("")){
                        int quantityInt = Integer.valueOf(quantity);
                        if(quantityInt<=0){
                            Toast.makeText(ProductDetail.this, "Quantity must be more then 0", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            orderBean = new OrderBean();
                            orderBean.setQuantity(quantityInt);
                        }
                    }


                    orderBean.setOrderUid(CommonUtility.generateUID());
                    orderBean.setProductName(productBean.getName());
                    orderBean.setProductUid(productBean.getProductUid());
                    orderBean.setUserUid(mAuth.getUid());
                    orderBean.setAddress("");
                    orderBean.setStatus("Cart");
                    orderBean.setColor(selectedColor!=null?selectedColor:0);
                    orderBean.setSize(selectedSize!=null?selectedSize:0);
                    orderBean.setImgUri(productBean.getImgUrl());
                    orderBean.setPrice(productBean.getPrice());
                    orderBean.setDealerUid(productBean.getDealerUid());
                    binding.btnAddToCart.setBackgroundTintList(colorgrey);
                    binding.btnTvAdd.setText("Remove Cart");

                    Toast.makeText(ProductDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();

                }else{
                    binding.btnAddToCart.setBackgroundTintList(colorOrange);
                    binding.btnTvAdd.setText("Add to Cart");
                    orderBean = null;
                    Toast.makeText(ProductDetail.this, "Remove from cart", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setView() {

        productBean = getIntent().getParcelableExtra("product");
        Glide.with(this)
                .load(productBean.getImgUrl())
                .placeholder(R.color.blueLight)
                .into(binding.ivProduct);

        binding.tvProductName.setText(productBean.getName());
        String price = getResources().getString(R.string.rupees);
        binding.tvPrice.setText(price+"\t"+productBean.getPrice());
        binding.tvDescription.setText(productBean.getDescription());
        if(productBean.getSizes().size()>0){
            binding.sizeContainer.setVisibility(View.VISIBLE);
            binding.proS.setVisibility(productBean.getSizes().contains(1L)?View.VISIBLE:View.GONE);
            binding.proM.setVisibility(productBean.getSizes().contains(2L)?View.VISIBLE:View.GONE);
            binding.proL.setVisibility(productBean.getSizes().contains(3L)?View.VISIBLE:View.GONE);
            binding.proXL.setVisibility(productBean.getSizes().contains(4L)?View.VISIBLE:View.GONE);
        }else{
            binding.sizeContainer.setVisibility(View.GONE);
        }



        isAlreadyInCart();




    }

    private void isAlreadyInCart() {
        dialogClass.progressShow("Loading..");
        db.collection("Orders")
                .whereEqualTo("status", "Cart")
                .whereEqualTo("userUid", mAuth.getUid())
                .whereEqualTo("productUid", productBean.getProductUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                orderBean = new OrderBean();
                                orderBean.setProductUid(document.getString("productUid"));
                                orderBean.setOrderUid(document.getString("orderUid"));
                                orderBean.setUserUid(document.getString("userUid"));
                                orderBean.setSize(Math.toIntExact((Long) document.get("size")));
                                orderBean.setColor(Math.toIntExact((Long) document.get("color")));
                                orderBean.setStatus(document.getString("status"));
                                orderBean.setAddress("");
                                orderBean.setQuantity(Math.toIntExact((Long) document.get("quantity")));
                                orderBean.setImgUri(document.getString("imgUri"));
                                orderBean.setPrice(document.getString("price"));
                                orderBean.setDealerUid(document.getString("dealerUid"));
                                isExistInCart = true;
                                existCartBean = orderBean;
                                updateView();
                            }

                        } else {
                            dialogClass.progresshide();
                        }
                    }
                });

    }

    private void updateView() {

        switch (orderBean.getSize()){
            case 1 :
                binding.proS.callOnClick();
                break;
            case 2:
                binding.proM.callOnClick();
                break;
            case 3 :
                binding.proL.callOnClick();
                break;
            case 4:
                binding.proXL.callOnClick();
        }

        switch (orderBean.getColor()){
            case 1 :
                binding.btnWhite.callOnClick();
                break;
            case 2:
                binding.btnBlack.callOnClick();
                break;
            case 3 :
                binding.btnBlue.callOnClick();
                break;
            case 4:
                binding.btnRed.callOnClick();
                break;
            case 5:
                binding.btnRed.callOnClick();
                break;
        }

        binding.etQuantity.setText(String.valueOf(orderBean.getQuantity()));
        binding.btnAddToCart.setBackgroundTintList(colorgrey);
        binding.btnTvAdd.setText("Remove from cart");

        dialogClass.progresshide();


    }

    @SuppressLint("ResourceAsColor")
    public void setDefaultViewState(){
        binding.proS.setBackgroundTintList(colorgrey);
        binding.proM.setBackgroundTintList(colorgrey);
        binding.proL.setBackgroundTintList(colorgrey);
        binding.proXL.setBackgroundTintList(colorgrey);
        binding.proS.setTextColor(getResources().getColor(R.color.grey));
        binding.proM.setTextColor(getResources().getColor(R.color.grey));
        binding.proL.setTextColor(getResources().getColor(R.color.grey));
        binding.proXL.setTextColor(getResources().getColor(R.color.grey));
    }

   public void unCheckAllColors(){
       binding.btnWhite.setBackgroundResource(R.drawable.card_background);
       binding.btnBlack.setBackgroundResource(R.drawable.card_background);
       binding.btnRed.setBackgroundResource(R.drawable.card_background);
       binding.btnBlue.setBackgroundResource(R.drawable.card_background);
       binding.btnGreen.setBackgroundResource(R.drawable.card_background);

   }


    @Override
    public void onBackPressed() {


        if (orderBean != null && !isExistInCart) {
            dialogClass.progressShow("Adding to cart..");
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderUid", orderBean.getOrderUid());
            orderData.put("productUid", orderBean.getProductUid());
            orderData.put("userUid", orderBean.getUserUid());
            orderData.put("productName", orderBean.getProductName());
            orderData.put("quantity", orderBean.getQuantity());
            orderData.put("size", orderBean.getSize());
            orderData.put("color", orderBean.getColor());
            orderData.put("address", orderBean.getAddress());
            orderData.put("status", orderBean.getStatus());
            orderData.put("imgUri",orderBean.getImgUri());
            orderData.put("price",orderBean.getPrice());
            orderData.put("dealerUid",orderBean.getDealerUid());
            db.collection("Orders").document(orderBean.getOrderUid())
                    .set(orderData)
                    .addOnSuccessListener(aVoid -> {
                        dialogClass.progresshide();
                        Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        dialogClass.progresshide();
                        Toast.makeText(this, "Failed to add cart", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }


        else if (orderBean == null && isExistInCart){
            dialogClass.progressShow("Removing from cart..");
            db.collection("Orders").document(existCartBean.getOrderUid())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialogClass.progresshide();
                            Toast.makeText(getApplicationContext(), "Removed from cart", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialogClass.progresshide();
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });

        }else{
            finish();
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