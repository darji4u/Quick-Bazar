package com.nareshdarji.quickbazar.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.databinding.ItemAddressBinding;

import java.util.ArrayList;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.viewHolder> {

    List<String> addressList = new ArrayList<>();
    String action = "SHOW";

    public AddressAdapter(List<String> addressList, String action) {
        this.addressList = addressList;
        this.action = action;
    }

    @NonNull
    @Override
    public AddressAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address,parent,false);
        return new AddressAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.viewHolder holder, int position) {
        int pos = position;
        holder.binding.tvAddress.setText(addressList.get(position));

        holder.binding.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressList.remove(pos);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class viewHolder  extends  RecyclerView.ViewHolder{

        ItemAddressBinding binding;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemAddressBinding.bind(itemView);
            if(action.equals("SHOW")){
                binding.ivDelete.setVisibility(View.GONE);
            }else{
                binding.ivDelete.setVisibility(View.VISIBLE);
            }

        }
    }
}
