package com.nareshdarji.quickbazar.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nareshdarji.quickbazar.Bean.ProductBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.CommonUtility;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.Utility.NetworkConnectivity;
import com.nareshdarji.quickbazar.databinding.ActivityAddProductBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddProduct extends AppCompatActivity {


    ActivityAddProductBinding binding;
    Set<Integer> pColorsList;
    Set<Integer> pSize;
    Uri ProductImgUri = null;
    ProductBean productBean;
    int selectedCategory = 0;

    String customCategory = "";
    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DialogClass dialog;

    ArrayList<String> productCategory;

    NetworkConnectivity networkConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        pColorsList = new HashSet<>();
        pSize = new HashSet<>();
        dialog = new DialogClass(this);
        productCategory = new ArrayList<>();
        networkConnectivity = new NetworkConnectivity(this);
        setCategoryView();



        binding.btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pColorsList.contains(1)){
                    pColorsList.add(1);
                    binding.btnWhite.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    pColorsList.remove(1);
                    binding.btnWhite.setBackgroundResource(R.drawable.card_background);
                }
            }
        });
        binding.btnBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pColorsList.contains(2)){
                    pColorsList.add(2);
                    binding.btnBlack.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    pColorsList.remove(2);
                    binding.btnBlack.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        binding.btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pColorsList.contains(3)){
                    pColorsList.add(3);
                    binding.btnBlue.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    pColorsList.remove(3);
                    binding.btnBlue.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        binding.btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pColorsList.contains(4)){
                    pColorsList.add(4);
                    binding.btnRed.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    pColorsList.remove(4);
                    binding.btnRed.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        binding.btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pColorsList.contains(5)){
                    pColorsList.add(5);
                    binding.btnGreen.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    pColorsList.remove(5);
                    binding.btnGreen.setBackgroundResource(R.drawable.card_background);
                }
            }
        });


        binding.btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pSize.contains(1)){
                    pSize.add(1);
                    binding.btnS.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    pSize.remove(1);
                    binding.btnS.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        binding.btnM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pSize.contains(2)){
                    pSize.add(2);
                    binding.btnM.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    pSize.remove(2);
                    binding.btnM.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        binding.btnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pSize.contains(3)){
                    pSize.add(3);
                    binding.btnL.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    pSize.remove(3);
                    binding.btnL.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        binding.btnXL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!pSize.contains(4)){
                    pSize.add(4);
                    binding.btnXL.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    pSize.remove(4);
                    binding.btnXL.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        binding.ivProductImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProductData();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void setCategoryView() {
        productCategory.clear();
        productCategory.add("Custom");
        DocumentReference docRef = db.collection("MetaData").document("Product");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.exists()){
                    productCategory.addAll((ArrayList<String>) value.get("category"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_item, productCategory);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.ddCategory.setAdapter(adapter);
            }
        });

        binding.ddCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                binding.tvSelectedCategory.setText(selectedItem);
                selectedCategory = position;
                if(position==0){
                    binding.etCategory.setVisibility(View.VISIBLE);
                }else{
                    binding.etCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void saveProductData(){

        String productName = binding.etProductName.getText().toString().trim();
        String productDes = binding.etProductDescription.getText().toString().trim();
        String proPrice = binding.etPrize.getText().toString();
        String category = "";

        if(selectedCategory==0){
            category = binding.etCategory.getText().toString().trim();
        }else{
            category = binding.tvSelectedCategory.getText().toString().trim();
        }



        if(ProductImgUri==null){
            showErrorMessage("Please Select Product image");
            return;
        }
        else if(productName.equals("")){
            showErrorMessage("Please enter product name");
            return;
        }
        else if(productDes.equals("")){
            showErrorMessage("Please enter product description");
            return;
        }
        else if(proPrice.equals("")){
            showErrorMessage("Please enter price");
            return;
        }else if(!proPrice.equals("")){
            int price = Integer.valueOf(proPrice);
            if(price<=0){
                showErrorMessage("Invalid Price");
                return;
            }
        }
        if(selectedCategory==0 && category.equals("")){
            showErrorMessage("Please enter category");
            return;
        }

        dialog.progressShow("Uploading Product Details..");

        List<Integer> colorList =new ArrayList<>(pColorsList);
        List<Integer> sizeList = new ArrayList<>(pSize);

        productBean = new ProductBean(CommonUtility.generateUID(),mAuth.getUid(),productName,productDes,proPrice,category,"",colorList,sizeList);
        if(selectedCategory==0){
            updateCategory();
        }
        uploadProduct();

    }

    private void updateCategory() {

        productCategory.remove(0);
        productCategory.add(productBean.getCategory());

        DocumentReference docRef = db.collection("MetaData").document("Product");

        docRef.update("category", productCategory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });


    }


    private void uploadProduct() {
        if (ProductImgUri != null) {

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            storageReference = storageRef.child("Products/" + System.currentTimeMillis());

            storageReference.putFile(ProductImgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if(taskSnapshot!=null){
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uploadProductDetails(uri);
                                    }
                                });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.progresshide();
                            Toast.makeText(getApplicationContext(), "Somethins went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadProductDetails(Uri uri) {



        Map<String, Object> product = new HashMap<>();
        product.put("productUid", productBean.getProductUid());
        product.put("dealerUid",mAuth.getUid());
        product.put("imgUri",uri);
        product.put("name", productBean.getName());
        product.put("description", productBean.getDescription());
        product.put("price", productBean.getPrice());
        product.put("category", productBean.getCategory());
        product.put("colors", productBean.getColors());
        product.put("size", productBean.getSizes());
        product.put("availability", "Available");

        db.collection("Products").document(productBean.getProductUid())
                .set(product)
                .addOnSuccessListener(aVoid -> {
                    dialog.progresshide();
                    dialog.showMessageDialog(this,"Product Added Successfully","success");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },3000);

                })
                .addOnFailureListener(e -> {
                    dialog.progresshide();
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                });

    }


    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ProductImgUri = data.getData();
            binding.ivProductImg.setImageURI(ProductImgUri);
        }
    }


    public void showErrorMessage(String msg){
            binding.tvError.setText(msg);
            binding.tvError.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.tvError.setVisibility(View.GONE);
                }
            },3000);
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