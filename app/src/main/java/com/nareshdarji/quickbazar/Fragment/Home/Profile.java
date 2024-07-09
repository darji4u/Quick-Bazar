package com.nareshdarji.quickbazar.Fragment.Home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.nareshdarji.quickbazar.Activity.AddProduct;
import com.nareshdarji.quickbazar.Activity.DealerRequest;
import com.nareshdarji.quickbazar.Activity.EditProfile;
import com.nareshdarji.quickbazar.Activity.HomeActivity;
import com.nareshdarji.quickbazar.Activity.MyProducts;
import com.nareshdarji.quickbazar.Bean.UserBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.RecyclerView.AddressAdapter;
import com.nareshdarji.quickbazar.SplashScreen;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.databinding.FragmentProfileBinding;

import java.util.List;


public class Profile extends Fragment {


    FragmentProfileBinding binding;
    AddressAdapter addressAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    UserBean CurrentUserDetail;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        binding = FragmentProfileBinding.bind(inflater.inflate(R.layout.fragment_profile, container, false));
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getUserDetails();


        binding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });


        binding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddProduct.class));
            }
        });

        binding.btnMyProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyProducts.class));
            }
        });


        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                startActivity(new Intent(getActivity(), SplashScreen.class));
                getActivity().finishAffinity();
            }
        });

        binding.btnDealerRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DealerRequest.class));
            }
        });



        return binding.getRoot();
    }

    public void setView(){


        binding.tvUserName.setText("Hi, "+HomeActivity.CurrentUserDetail.getName());
        binding.tvEmail.setText(HomeActivity.CurrentUserDetail.getEmail());
        binding.tvUserMobile.setText(mAuth.getCurrentUser().getPhoneNumber());
        List<String> addressList = HomeActivity.CurrentUserDetail.getAddress();

        if(HomeActivity.CurrentUserDetail.isAuth()&&HomeActivity.CurrentUserDetail.getUserType().equals("Dealer")){
            binding.conDealer.setVisibility(View.VISIBLE);
            binding.tvShopName.setText(HomeActivity.CurrentUserDetail.getShopName());
            binding.tvShopAddress.setText(HomeActivity.CurrentUserDetail.getShopAddress());
            binding.btnAddProduct.setVisibility(View.VISIBLE);
            binding.btnMyProducts.setVisibility(View.VISIBLE);
        }else{
            binding.conDealer.setVisibility(View.GONE);
            binding.btnAddProduct.setVisibility(View.GONE);
            binding.btnMyProducts.setVisibility(View.GONE);
        }

        binding.tvUserType.setText(HomeActivity.CurrentUserDetail.getUserType());
        addressAdapter = new AddressAdapter(addressList,"SHOW");
        binding.rvAddress.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false));
        binding.rvAddress.setAdapter(addressAdapter);

        binding.btnEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogClass dialogClass = new DialogClass(getActivity());
                dialogClass.editAddressDialog(getActivity(),addressAdapter);
            }
        });

        if(HomeActivity.CurrentUserDetail.getMobile().equals("+918955535903")){
            binding.btnDealerRequest.setVisibility(View.VISIBLE);
        }else{
            binding.btnDealerRequest.setVisibility(View.GONE);
        }

        if(HomeActivity.CurrentUserDetail.getAuth() && HomeActivity.CurrentUserDetail.getUserType().equals("Dealer")){
            binding.tvAlertForApproval.setVisibility(View.GONE);
        }else{
            binding.tvAlertForApproval.setVisibility(View.VISIBLE);
        }


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

                        DocumentReference docRef = db.collection("shops").document(CurrentUserDetail.getShopUid());

                        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()){
                                    CurrentUserDetail.setShopName(documentSnapshot.getString("shopName"));
                                    CurrentUserDetail.setShopAddress(documentSnapshot.getString("shopAddress"));
                                    HomeActivity.CurrentUserDetail = CurrentUserDetail;
                                    setView();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });

                    }else{
                        CurrentUserDetail.setShopName("");
                        CurrentUserDetail.setShopAddress("");
                        setView();
                    }
                } else {
                    mAuth.signOut();
                }
            }
        });
    }


    @Override
    public void onResume() {
        setView();

        super.onResume();
    }
}