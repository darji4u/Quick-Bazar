package com.nareshdarji.quickbazar.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nareshdarji.quickbazar.Activity.HomeActivity;
import com.nareshdarji.quickbazar.Fragment.Home.Products;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.databinding.RvItemCategoryBinding;
import com.nareshdarji.quickbazar.interfacePackage.AdapterOnClick;
import com.nareshdarji.quickbazar.interfacePackage.onClick;

import java.util.ArrayList;
import java.util.List;

public class Category_Home extends RecyclerView.Adapter<Category_Home.viewHolder> {

    List<String> category = new ArrayList<>();
    AdapterOnClick adapterOnClick;
    Activity activity;

    public Category_Home(Activity activity,List<String> categoryList, AdapterOnClick onClick) {
        category = categoryList;
        this.adapterOnClick = onClick;
        this.activity = activity;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_category,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
            holder.binding.tvCategory.setText(category.get(position));

            if(Products.SelectedCategory.equalsIgnoreCase(category.get(position))){

                holder.binding.tvCategory.setBackgroundResource(R.drawable.card_background_blue);
                holder.binding.tvCategory.setTextColor(activity.getResources().getColor(R.color.blueDark));

            }else{
                holder.binding.tvCategory.setBackgroundResource(R.drawable.card_background);
                holder.binding.tvCategory.setTextColor(activity.getResources().getColor(R.color.black1));
            }

            holder.binding.tvCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapterOnClick.onClick(position);
                }
            });

    }

    @Override
    public int getItemCount() {
        return category.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        RvItemCategoryBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvItemCategoryBinding.bind(itemView);
        }
    }
}
