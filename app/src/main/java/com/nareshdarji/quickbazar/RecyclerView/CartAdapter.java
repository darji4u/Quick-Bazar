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
import com.google.firebase.firestore.FirebaseFirestore;
import com.nareshdarji.quickbazar.Bean.OrderBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.databinding.ItemCartLayoutBinding;
import com.nareshdarji.quickbazar.interfacePackage.OnQtyChange;
import com.nareshdarji.quickbazar.interfacePackage.onClick;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.viewHolder> {

    List<OrderBean> list = new ArrayList<>();
    Activity activity;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    OnQtyChange onQtyChange;
    public CartAdapter(List<OrderBean> cartList,Activity activity, OnQtyChange qtyChange) {
        list = cartList;
        this.activity = activity;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        onQtyChange = qtyChange;
    }

    @NonNull
    @Override
    public CartAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_layout,parent,false);
        return new CartAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.viewHolder holder, int position) {

        int pos = position;

        Glide.with(activity)
                .load(list.get(position).getImgUri())
                .placeholder(R.color.blueLight)
                .into(holder.binding.ivProduct);
        holder.binding.tvProductName.setText(list.get(position).getProductName());
        holder.binding.tvQuantity.setText(String.valueOf(list.get(position).getQuantity()));
        String price = activity.getResources().getString(R.string.rupees);
        holder.binding.tvAmount.setText(price+"\t"+list.get(pos).getPrice());
        holder.binding.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.valueOf(holder.binding.tvQuantity.getText().toString());
                if (qty<99){
                    qty++;
                    holder.binding.tvQuantity.setText(String.valueOf(qty));
                    onQtyChange.onQtyChange(list.get(pos).getPrice(),"Add");
                    list.get(pos).setQuantity(qty);
                }
            }
        });

        holder.binding.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.valueOf(holder.binding.tvQuantity.getText().toString());
                if (qty>1){
                    qty--;
                    holder.binding.tvQuantity.setText(String.valueOf(qty));
                    onQtyChange.onQtyChange(list.get(pos).getPrice(),"minus");
                    list.get(pos).setQuantity(qty);
                }
            }
        });

        holder.binding.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCart(pos);
            }
        });



    }

    private void removeCart(int pos) {

        int amount = Integer.valueOf(list.get(pos).getPrice())*Integer.valueOf(list.get(pos).getQuantity());

        DialogClass dialogClass = new DialogClass(activity);
        dialogClass.showConfirmationDialog(activity, "Are you sure you want to remove from cart", "Yes",new onClick() {
            @Override
            public void yesClick() {

                dialogClass.progressShow("Removing from cart..");
                db.collection("Orders").document(list.get(pos).getOrderUid())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialogClass.progresshide();
                                Toast.makeText(activity, "Removed from cart", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                                onQtyChange.onQtyChange(String.valueOf(amount),"minus");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialogClass.progresshide();
                                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        ItemCartLayoutBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartLayoutBinding.bind(itemView);
        }
    }
}
