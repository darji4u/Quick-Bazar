package com.nareshdarji.quickbazar.RecyclerView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nareshdarji.quickbazar.Bean.OrderBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.databinding.ItemOrderHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class MyOrderHistoryAdapter extends RecyclerView.Adapter<MyOrderHistoryAdapter.viewHolder> {


    List<OrderBean> list = new ArrayList<>();
    Activity activity;

    public MyOrderHistoryAdapter(List<OrderBean> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyOrderHistoryAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history,parent,false);
        return new MyOrderHistoryAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderHistoryAdapter.viewHolder holder, int position) {
        OrderBean bean = list.get(position);

        Glide.with(activity)
                .load(bean.getImgUri())
                .placeholder(R.color.blueLight)
                .into(holder.binding.ivProduct);

        holder.binding.tvProductName.setText(bean.getProductName());
        holder.binding.tvQtyAndAmount.setText(bean.getPrice()+" * "+bean.getQuantity());
        String price = activity.getResources().getString(R.string.rupees);
        holder.binding.tvTotalAmount.setText(price+"\t"+String.valueOf((Integer.valueOf(bean.getQuantity()))*(Integer.parseInt(bean.getPrice()))));
        holder.binding.tvAddress.setText(bean.getAddress());
        if(bean.getStatus().equals("Confirm")){
            holder.binding.tvStatus.setTextColor(activity.getResources().getColor(R.color.blueDark));
            holder.binding.tvStatus.setText("On The Way");
        }else{
            holder.binding.tvStatus.setTextColor(activity.getResources().getColor(R.color.green));
            holder.binding.tvStatus.setText("Delivered");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ItemOrderHistoryBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOrderHistoryBinding.bind(itemView);
        }
    }
}
