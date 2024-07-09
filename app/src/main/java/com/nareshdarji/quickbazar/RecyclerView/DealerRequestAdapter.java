package com.nareshdarji.quickbazar.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nareshdarji.quickbazar.Activity.HomeActivity;
import com.nareshdarji.quickbazar.Bean.UserBean;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.Utility.DialogClass;
import com.nareshdarji.quickbazar.databinding.ItemDealerRequestBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DealerRequestAdapter extends RecyclerView.Adapter<DealerRequestAdapter.viewHolder> {


    List<UserBean> list = new ArrayList<>();
    Activity activity;
    ColorStateList blue;
    ColorStateList red;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public DealerRequestAdapter(List<UserBean> list, Activity activity) {
        this.list = list;
        this.activity = activity;
        blue = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.blueDark));
        red = ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.red));

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


    }

    @NonNull
    @Override
    public DealerRequestAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dealer_request,parent,false);
        return new DealerRequestAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealerRequestAdapter.viewHolder holder, int position) {

        UserBean userBean = list.get(position);
        holder.binding.tvUserName.setText(userBean.getName());
        holder.binding.tvMobileNo.setText(userBean.getMobile());

        holder.binding.btnAction.setText(userBean.getAuth()?"Disable":"Approve");
        holder.binding.btnAction.setBackgroundTintList(userBean.getAuth()?red:blue);

        holder.binding.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserStatus(userBean);
            }
        });


    }

    private void updateUserStatus(UserBean userBean) {

        DialogClass dialogClass = new DialogClass(activity);

        dialogClass.progressShow("Please Wait...");
        Map<String, Object> userData = new HashMap<>();
        userData.put("auth", !userBean.getAuth());


        db.collection("users").document(userBean.getUserUid())
                .update(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(activity, "Approved Successfully", Toast.LENGTH_SHORT).show();
                    dialogClass.progresshide();
                })
                .addOnFailureListener(e -> {
                    dialogClass.progresshide();
                    Toast.makeText(activity, "Failed to approve", Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        ItemDealerRequestBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDealerRequestBinding.bind(itemView);
        }
    }
}
