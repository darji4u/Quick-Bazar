package com.nareshdarji.quickbazar.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.opengl.EGLImage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ForwardingListeningExecutorService;
import com.nareshdarji.quickbazar.Activity.ProductDetail;
import com.nareshdarji.quickbazar.Bean.ProductBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.databinding.RvItemProductHomeBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.viewHolder> {


    Activity activity;
    List<ProductBean> list;

    public ProductAdapter(Activity activity, List<ProductBean> productList) {
        this.activity = activity;
        list = productList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_product_home,parent,false);
        return new ProductAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.binding.itemProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.get(position).getAvailability().equals("Available")){
                    Intent intent = new Intent(activity, ProductDetail.class);
                    intent.putExtra("product", list.get(position));
                    activity.startActivity(intent);
                }
            }
        });

        holder.binding.tvName.setText(list.get(position).getName());
        String price = activity.getResources().getString(R.string.rupees);
        holder.binding.tvPrice.setText(price+"\t"+list.get(position).getPrice());
        holder.binding.btnEdit.setVisibility(View.GONE);

        Glide.with(activity)
                .load(list.get(position).getImgUrl())
                .placeholder(R.color.blueLight)
                .into(holder.binding.ivProductHome);


       holder.binding.tvAvailable.setVisibility(list.get(position).getAvailability().equals("Available")?View.GONE:View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        RvItemProductHomeBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RvItemProductHomeBinding.bind(itemView);
        }
    }

}