package com.nareshdarji.quickbazar.Fragment.Home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nareshdarji.quickbazar.Bean.ProductBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.RecyclerView.Category_Home;
import com.nareshdarji.quickbazar.RecyclerView.ProductAdapter;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.Utility.FireBaseOperations;
import com.nareshdarji.quickbazar.databinding.FragmentProductsBinding;
import com.nareshdarji.quickbazar.interfacePackage.AdapterOnClick;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Products extends Fragment {


    FragmentProductsBinding binding;
    Category_Home adapterCategory;
    ProductAdapter productAdapter;
    List<String> categoryList;
    List<ProductBean> productList;
    List<ProductBean> adapterList;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    DialogClass dialogClass;

    public static String SelectedCategory = "All";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProductsBinding.bind(inflater.inflate(R.layout.fragment_products, container, false));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        categoryList = new ArrayList<>();

        productList = new ArrayList<>();
        adapterList = new ArrayList<>();
        dialogClass = new DialogClass(getActivity());

        categoryList.add(0,"All");
        adapterCategory = new Category_Home(getActivity(),categoryList, new AdapterOnClick() {
            @Override
            public void onClick(int pos) {

                SelectedCategory = categoryList.get(pos);
                filterData();
            }
        });




        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    String searchVal = editable.toString();
                    if(searchVal.equals("")){
                        filterData();
                    }else{
                        adapterList.clear();
                        for(ProductBean productBean : productList){
                            if (productBean.getName().toLowerCase().contains(searchVal.toLowerCase())) {
                                adapterList.add(productBean);
                            }
                        }

                    }

                    productAdapter.notifyDataSetChanged();

            }
        });

        binding.rvCategory.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL,false));
        binding.rvCategory.setAdapter(adapterCategory);


        productAdapter = new ProductAdapter(getActivity(), adapterList);
        binding.rvProductsHome.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.rvProductsHome.setAdapter(productAdapter);

        setCategory();
        setProductView();

        return binding.getRoot();
    }

    private void filterData() {

        dialogClass.progressShow("Please Wait..");
        if(SelectedCategory.equalsIgnoreCase("all")){
            adapterList.clear();
            adapterList.addAll(productList);
        }else{
            adapterList.clear();
            for(ProductBean bean : productList){
                if(bean.getCategory().equalsIgnoreCase(SelectedCategory)){
                    adapterList.add(bean);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
        adapterCategory.notifyDataSetChanged();
        dialogClass.progresshide();

    }

    private void setProductView() {

        CollectionReference colRef = db.collection("Products");
        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(!value.isEmpty()){
                        productList.clear();
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


                        if(SelectedCategory.equalsIgnoreCase("all")){
                            adapterList.clear();
                            adapterList.addAll(productList);
                        }else{
                            adapterList.clear();
                            for(ProductBean bean : productList){
                                if(bean.getCategory().equalsIgnoreCase(SelectedCategory)){
                                    adapterList.add(bean);
                                }

                            }
                        }

                        productAdapter.notifyDataSetChanged();
                    }
            }
        });


    }

    private void setCategory() {
        DocumentReference docRef = db.collection("MetaData").document("Product");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    categoryList.addAll((ArrayList<String>) value.get("category"));
                    adapterCategory.notifyDataSetChanged();
                }
            }
        });

    }
}