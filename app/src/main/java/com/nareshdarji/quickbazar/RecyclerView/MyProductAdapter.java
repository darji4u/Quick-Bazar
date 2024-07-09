package com.nareshdarji.quickbazar.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nareshdarji.quickbazar.Activity.HomeActivity;
import com.nareshdarji.quickbazar.Activity.ProductDetail;
import com.nareshdarji.quickbazar.Bean.ProductBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.databinding.DialogEditProductDetailBinding;
import com.nareshdarji.quickbazar.databinding.RvItemProductHomeBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.viewHolder> {

    List<ProductBean> list;
    Activity activity;

    ColorStateList colorOrange;
    ColorStateList colorgrey;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String[] available;

    public MyProductAdapter(Activity activity, List<ProductBean> productList) {

        this.list = productList;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


    }

    @NonNull
    @Override
    public MyProductAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_product_home,parent,false);
        return new MyProductAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProductAdapter.viewHolder holder, int position) {

        int pos = position;
        holder.binding.itemProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ProductDetail.class);
                intent.putExtra("product", list.get(position));
                activity.startActivity(intent);
            }
        });

        holder.binding.tvName.setText(list.get(position).getName());

        String price = activity.getResources().getString(R.string.rupees);
        holder.binding.tvPrice.setText(price+"\t"+list.get(position).getPrice());
        holder.binding.btnEdit.setVisibility(View.VISIBLE);
        holder.binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(list.get(pos));
            }
        });

        holder.binding.tvAvailable.setVisibility(list.get(pos).getAvailability().equalsIgnoreCase("Available")?View.GONE:View.VISIBLE);

        Glide.with(activity)
                .load(list.get(position).getImgUrl())
                .placeholder(R.color.blueLight)
                .into(holder.binding.ivProductHome);

    }

    private void showEditDialog(ProductBean productBean) {

        Set<Integer> sizeList = new HashSet<>();
        Set<Integer> colorList = new HashSet<>();


        if (productBean.getColors().contains(1L)) {
            colorList.add(1);
        }
        if (productBean.getColors().contains(2L)) {
            colorList.add(2);
        }
        if (productBean.getColors().contains(3L)) {
            colorList.add(3);
        }
        if (productBean.getColors().contains(4L)) {
            colorList.add(4);
        }
        if (productBean.getColors().contains(5L)) {
            colorList.add(5);
        }

        if (productBean.getSizes().contains(1L)) {
            sizeList.add(1);
        }
        if (productBean.getSizes().contains(2L)) {
            sizeList.add(2);
        }
        if (productBean.getSizes().contains(3L)) {
            sizeList.add(3);
        }
        if (productBean.getSizes().contains(4L)) {
            sizeList.add(4);
        }


        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.dialog_edit_product_detail, null);
        DialogEditProductDetailBinding dialogBinding = DialogEditProductDetailBinding.bind(view);

        Dialog dialog = new Dialog(activity, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);

        setDialogView(productBean,dialogBinding);


        dialogBinding.btnWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!colorList.contains(1)){
                    colorList.add(1);
                    dialogBinding.btnWhite.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    colorList.remove(1);
                    dialogBinding.btnWhite.setBackgroundResource(R.drawable.card_background);
                }
            }
        });
        dialogBinding.btnBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!colorList.contains(2)){
                    colorList.add(2);
                    dialogBinding.btnBlack.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    colorList.remove(2);
                    dialogBinding.btnBlack.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        dialogBinding.btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!colorList.contains(3)){
                    colorList.add(3);
                    dialogBinding.btnBlue.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    colorList.remove(3);
                    dialogBinding.btnBlue.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        dialogBinding.btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!colorList.contains(4)){
                    colorList.add(4);
                    dialogBinding.btnRed.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    colorList.remove(4);
                    dialogBinding.btnRed.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        dialogBinding.btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!colorList.contains(5)){
                    colorList.add(5);
                    dialogBinding.btnGreen.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    colorList.remove(5);
                    dialogBinding.btnGreen.setBackgroundResource(R.drawable.card_background);
                }
            }
        });


        dialogBinding.btnS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sizeList.contains(1)){
                    sizeList.add(1);
                    dialogBinding.btnS.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    sizeList.remove(1);
                    dialogBinding.btnS.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        dialogBinding.btnM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sizeList.contains(2)){
                    sizeList.add(2);
                    dialogBinding.btnM.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    sizeList.remove(2);
                    dialogBinding.btnM.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        dialogBinding.btnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sizeList.contains(3)){
                    sizeList.add(3);
                    dialogBinding.btnL.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    sizeList.remove(3);
                    dialogBinding.btnL.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        dialogBinding.btnXL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sizeList.contains(4)){
                    sizeList.add(4);
                    dialogBinding.btnXL.setBackgroundResource(R.drawable.card_background_blue);
                }else{
                    sizeList.remove(4);
                    dialogBinding.btnXL.setBackgroundResource(R.drawable.card_background);
                }
            }
        });

        dialogBinding.btnAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBinding.btnAvailable.setBackgroundResource(R.drawable.card_background_blue);
                dialogBinding.btnAvailable.setTextColor(activity.getResources().getColor(R.color.blueDark));
                dialogBinding.btnUnAvailable.setBackgroundResource(R.drawable.card_background);
                dialogBinding.btnUnAvailable.setTextColor(activity.getResources().getColor(R.color.black1));
                available[0] = "Available";
            }
        });

        dialogBinding.btnUnAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBinding.btnUnAvailable.setBackgroundResource(R.drawable.card_background_blue);
                dialogBinding.btnUnAvailable.setTextColor(activity.getResources().getColor(R.color.blueDark));
                dialogBinding.btnAvailable.setBackgroundResource(R.drawable.card_background);
                dialogBinding.btnAvailable.setTextColor(activity.getResources().getColor(R.color.black1));
                available[0] = "Unavailable";
            }
        });



        dialogBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });


        dialogBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String proPrice = dialogBinding.etPrize.getText().toString();

                if(proPrice.equals("")){
                    dialogBinding.errorMsg.setVisibility(View.VISIBLE);
                    dialogBinding.errorMsg.setText("Please Enter Price");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialogBinding.errorMsg.setVisibility(View.GONE);
                        }
                    },3000);
                    return;
                }else if(!proPrice.equals("")){
                    int price = Integer.valueOf(proPrice);
                    if(price<=0){
                        dialogBinding.errorMsg.setText("Price must be greater then 0");
                        dialogBinding.errorMsg.setVisibility(View.VISIBLE);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialogBinding.errorMsg.setVisibility(View.GONE);
                            }
                        },3000);

                        return;
                    }
                }
                dialog.cancel();
                DialogClass dialogClass = new DialogClass(activity);
                dialogClass.progressShow("Updating...");
                Map<String, Object> productObj = new HashMap<>();
                productObj.put("price", proPrice );

                List<Integer> colors = new ArrayList<>(colorList);
                List<Integer> sizes = new ArrayList<>(sizeList);

                productObj.put("colors", colors);
                productObj.put("size", sizes);
                productObj.put("availability", available[0]);
                db.collection("Products").document(productBean.getProductUid())
                        .update(productObj)
                        .addOnSuccessListener(aVoid -> {
                            dialogClass.progresshide();
                            dialogClass.showMessageDialog(activity,"Product Details Update Successfully","success");

                        })
                        .addOnFailureListener(e -> {
                            dialogClass.progresshide();
                            Toast.makeText(activity, "Failed to add user data to Firestore", Toast.LENGTH_SHORT).show();
                        });
            }
        });


        dialog.show();
    }

    private void setDialogView(ProductBean productBean, DialogEditProductDetailBinding dialogBinding) {

        dialogBinding.tvProductName.setText(productBean.getName());
        dialogBinding.etPrize.setText(productBean.getPrice());

        List<Integer> colorList = productBean.getColors();
        List<Integer> sizeList = productBean.getSizes();

        dialogBinding.btnWhite.setBackgroundResource(colorList.contains(1L)?R.drawable.card_background_blue:R.drawable.card_background);
        dialogBinding.btnBlack.setBackgroundResource(colorList.contains(2L)?R.drawable.card_background_blue:R.drawable.card_background);
        dialogBinding.btnBlue.setBackgroundResource(colorList.contains(3L)?R.drawable.card_background_blue:R.drawable.card_background);
        dialogBinding.btnRed.setBackgroundResource(colorList.contains(4L)?R.drawable.card_background_blue:R.drawable.card_background);
        dialogBinding.btnGreen.setBackgroundResource(colorList.contains(5L)?R.drawable.card_background_blue:R.drawable.card_background);

        dialogBinding.btnS.setBackgroundResource((sizeList.contains(1L))?(R.drawable.card_background_blue):(R.drawable.card_background));
        dialogBinding.btnM.setBackgroundResource(sizeList.contains(2L)?R.drawable.card_background_blue:R.drawable.card_background);
        dialogBinding.btnL.setBackgroundResource(sizeList.contains(3L)?R.drawable.card_background_blue:R.drawable.card_background);
        dialogBinding.btnXL.setBackgroundResource(sizeList.contains(4L)?R.drawable.card_background_blue:R.drawable.card_background);

        if(productBean.getAvailability().equals("Available")){
            dialogBinding.btnAvailable.setBackgroundResource(R.drawable.card_background_blue);
            dialogBinding.btnAvailable.setTextColor(activity.getResources().getColor(R.color.blueDark));
            dialogBinding.btnUnAvailable.setBackgroundResource(R.drawable.card_background);
            dialogBinding.btnUnAvailable.setTextColor(activity.getResources().getColor(R.color.black1));

        }else{
            dialogBinding.btnUnAvailable.setBackgroundResource(R.drawable.card_background_blue);
            dialogBinding.btnUnAvailable.setTextColor(activity.getResources().getColor(R.color.blueDark));
            dialogBinding.btnAvailable.setBackgroundResource(R.drawable.card_background);
            dialogBinding.btnAvailable.setTextColor(activity.getResources().getColor(R.color.black1));
        }

        available = new String[]{productBean.getAvailability()};

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        RvItemProductHomeBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvItemProductHomeBinding.bind(itemView);
        }
    }
}
