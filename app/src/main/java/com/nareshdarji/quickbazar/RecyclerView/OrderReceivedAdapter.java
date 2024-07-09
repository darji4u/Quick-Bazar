package com.nareshdarji.quickbazar.RecyclerView;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nareshdarji.quickbazar.Activity.HomeActivity;
import com.nareshdarji.quickbazar.Bean.OrderBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.databinding.ItemOrderReceiverBinding;
import com.nareshdarji.quickbazar.interfacePackage.onClick;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderReceivedAdapter extends RecyclerView.Adapter<OrderReceivedAdapter.viewHolder> {

    List<OrderBean> list;
    Activity activity;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public OrderReceivedAdapter(Activity activity, List<OrderBean> adapterList) {
        list = adapterList;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderReceivedAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_receiver,parent,false);
        return new OrderReceivedAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderReceivedAdapter.viewHolder holder, int position) {
        OrderBean bean = list.get(position);

        Glide.with(activity)
                .load(bean.getImgUri())
                .placeholder(R.color.blueLight)
                .into(holder.binding.ivProduct);

        holder.binding.tvProductName.setText(bean.getProductName());
        holder.binding.tvQtyAndAmount.setText(bean.getPrice()+" * "+bean.getQuantity());
        holder.binding.tvName.setText(bean.getUserName());
        holder.binding.tvAddress.setText(bean.getAddress());
        holder.binding.tvMobile.setText(bean.getMobile());

        String price = activity.getResources().getString(R.string.rupees);
        holder.binding.tvTotalAmount.setText(price+"\t"+String.valueOf((Integer.valueOf(bean.getQuantity()))*(Integer.parseInt(bean.getPrice()))));

        if(bean.getStatus().equals("Confirm")){
            holder.binding.btnDelivered.setVisibility(View.VISIBLE);
        }else{
            holder.binding.btnDelivered.setVisibility(View.GONE);
        }

        holder.binding.btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDeliveredStatus(bean);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ItemOrderReceiverBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemOrderReceiverBinding.bind(itemView);
        }
    }

    private void updateDeliveredStatus(OrderBean bean) {

        DialogClass dialogClass = new DialogClass(activity);
        dialogClass.showConfirmationDialog(activity, "Is this product delivered ?", "Yes",new onClick() {
            @Override
            public void yesClick() {
                dialogClass.progressShow("Please Wait...");
                DocumentReference docRef = db.collection("Orders").document(bean.getOrderUid());
                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "Delivered");
                docRef.update(updates)
                        .addOnSuccessListener(bVoid -> {
                            dialogClass.progresshide();
                            dialogClass.showMessageDialog(activity,"Order Delivered Successfully","success");
                        })
                        .addOnFailureListener(e -> {
                            dialogClass.progresshide();
                            Toast.makeText(activity, "error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

}
