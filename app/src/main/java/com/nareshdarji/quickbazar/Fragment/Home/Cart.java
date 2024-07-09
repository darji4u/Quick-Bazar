package com.nareshdarji.quickbazar.Fragment.Home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nareshdarji.quickbazar.Activity.HomeActivity;
import com.nareshdarji.quickbazar.Bean.OrderBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.RecyclerView.CartAdapter;
import com.nareshdarji.quickbazar.Utility.CommonUtility;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.databinding.ConfirmationDialogBinding;
import com.nareshdarji.quickbazar.databinding.DialogOrderLayoutBinding;
import com.nareshdarji.quickbazar.databinding.FragmentCartBinding;
import com.nareshdarji.quickbazar.interfacePackage.OnQtyChange;
import com.nareshdarji.quickbazar.interfacePackage.onClick;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Cart extends Fragment {


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    FragmentCartBinding binding;
    CartAdapter cartAdapter;
    List<OrderBean> cartList;

    DialogClass dialogClass;
    int totalProducts = 0;
    int totalPrice = 0;
    String orderAddress = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.bind(inflater.inflate(R.layout.fragment_cart, container, false));
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cartList = new ArrayList<>();
        dialogClass = new DialogClass(getActivity());
        getCartList();

        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderPlace();
            }
        });


        cartAdapter = new CartAdapter(cartList, getActivity(), new OnQtyChange() {
            @Override
            public void onQtyChange(String amount, String action) {
                if(action.equals("Add")){
                    int val = Integer.valueOf(binding.tvTotalAmount.getText().toString())+Integer.valueOf(amount);
                    binding.tvTotalAmount.setText(String.valueOf(val));
                }else{
                    int val = Integer.valueOf(binding.tvTotalAmount.getText().toString())-(Integer.valueOf(amount));
                    binding.tvTotalAmount.setText(String.valueOf(val));
                }
                binding.tvTotalProducts.setText(String.valueOf(cartList.size()));
                binding.summeryContainer.setVisibility(cartList.size()==0?View.GONE:View.VISIBLE);
            }
        });
        binding.rvCartItem.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false));
        binding.rvCartItem.setAdapter(cartAdapter);

        return binding.getRoot();
    }

    private void orderPlace() {
        List<String> addressList = HomeActivity.CurrentUserDetail.getAddress();

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_order_layout, null);
        DialogOrderLayoutBinding dialogBinding = DialogOrderLayoutBinding.bind(view);

        Dialog dialog = new Dialog(getActivity(), androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);
        if(addressList.size()>0){
            dialogBinding.addr1.setVisibility(View.VISIBLE);
            dialogBinding.tvAdd1.setText(addressList.get(0));
        }
        if(addressList.size()>1){
            dialogBinding.addr2.setVisibility(View.VISIBLE);
            dialogBinding.tvadd2.setText(addressList.get(1));
        }
        if(addressList.size()>2){
            dialogBinding.addr3.setVisibility(View.VISIBLE);
            dialogBinding.tvadd3.setText(addressList.get(2));
        }

        dialogBinding.tvTotalProducts.setText(String.valueOf(cartList.size()));
        dialogBinding.tvTotalAmount.setText(binding.tvTotalAmount.getText().toString());


        dialogBinding.addr1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBinding.addr1.setBackgroundResource(R.drawable.card_background_blue);
                dialogBinding.addr2.setBackgroundResource(R.drawable.card_background);
                dialogBinding.addr3.setBackgroundResource(R.drawable.card_background);
                orderAddress = dialogBinding.tvAdd1.getText().toString();
            }
        });

        dialogBinding.addr2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBinding.addr1.setBackgroundResource(R.drawable.card_background);
                dialogBinding.addr2.setBackgroundResource(R.drawable.card_background_blue);
                dialogBinding.addr3.setBackgroundResource(R.drawable.card_background);
                orderAddress = dialogBinding.tvadd2.getText().toString();
            }
        });

        dialogBinding.addr3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBinding.addr1.setBackgroundResource(R.drawable.card_background);
                dialogBinding.addr2.setBackgroundResource(R.drawable.card_background);
                dialogBinding.addr3.setBackgroundResource(R.drawable.card_background_blue);
                orderAddress = dialogBinding.tvadd3.getText().toString();
            }
        });


        dialogBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialogBinding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderAddress==null){
                    dialogBinding.errorMsg.setVisibility(View.VISIBLE);
                    dialogBinding.errorMsg.setText("Please select address");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialogBinding.errorMsg.setVisibility(View.GONE);
                        }
                    },3000);
                    return;
                }
                else
                {
                    dialog.cancel();
                    dialogClass.progressShow("Please Wait..");
                    String billUid = CommonUtility.generateUID();
                    LocalDateTime currentDateTime;

                    Map<String,Object> billData = new HashMap<>();
                    billData.put("billUid", billUid);
                    billData.put("totalProducts",cartList.size());
                    billData.put("totalAmount",dialogBinding.tvTotalAmount.getText().toString());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        currentDateTime = LocalDateTime.now();
                        billData.put("confirmDate",currentDateTime);
                    }
                    billData.put("address",orderAddress);
                    billData.put("clientUid",mAuth.getUid());

                    db.collection("BillData").document(billUid)
                            .set(billData)
                            .addOnSuccessListener(aVoid -> {
                                for (OrderBean orderBean : cartList){

                                    DocumentReference docRef = db.collection("Orders").document(orderBean.getOrderUid());

                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("status", "Confirm");
                                    updates.put("mobile",mAuth.getCurrentUser().getPhoneNumber());
                                    updates.put("userName",HomeActivity.CurrentUserDetail.getName());
                                    updates.put("address",orderAddress);
                                    updates.put("billUid", billUid);
                                    updates.put("quantity", orderBean.getQuantity());

                                    docRef.update(updates)
                                            .addOnSuccessListener(bVoid -> {
                                                dialogClass.progresshide();
                                                dialogClass.showMessageDialog(getActivity(),"Order Placed Successfully","success");
                                                getCartList();
                                            })
                                            .addOnFailureListener(e -> {
                                                dialogClass.progresshide();
                                                Toast.makeText(getActivity(), "error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });


                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getActivity(), "Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });


        dialog.show();

    }

    private void getCartList() {
        db.collection("Orders")
                .whereEqualTo("status", "Cart")
                .whereEqualTo("userUid", mAuth.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        cartList.clear();
                        if (!value.isEmpty()) {
                            binding.noDataAvailable.setVisibility(View.GONE);
                            binding.rvCartItem.setVisibility(View.VISIBLE);
                            for (DocumentSnapshot document : value.getDocuments()) {
                                OrderBean orderBean = new OrderBean();
                                orderBean.setProductUid(document.getString("productUid"));
                                orderBean.setProductName(document.getString("productName"));
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
                                cartList.add(orderBean);
                                totalPrice = totalPrice+(orderBean.getQuantity()*Integer.valueOf(orderBean.getPrice()));
                            }
                            totalProducts = cartList.size();
                            binding.tvTotalProducts.setText(String.valueOf(totalProducts));
                            binding.tvTotalAmount.setText(String.valueOf(totalPrice));
                            dialogClass.progresshide();
                            cartAdapter.notifyDataSetChanged();

                        } else {
                            binding.noDataAvailable.setVisibility(View.VISIBLE);
                            binding.rvCartItem.setVisibility(View.GONE);
                            binding.summeryContainer.setVisibility(cartList.size()==0?View.GONE:View.VISIBLE);
                        }
                    }
                });



    }
}