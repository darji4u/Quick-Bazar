package com.nareshdarji.quickbazar.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nareshdarji.quickbazar.Activity.HomeActivity;
import com.nareshdarji.quickbazar.R;
import com.nareshdarji.quickbazar.RecyclerView.AddressAdapter;
import com.nareshdarji.quickbazar.databinding.ConfirmationDialogBinding;
import com.nareshdarji.quickbazar.databinding.DialogAddAddressBinding;
import com.nareshdarji.quickbazar.databinding.DialogMessageBinding;
import com.nareshdarji.quickbazar.interfacePackage.onClick;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class DialogClass {

    Dialog progressDialog;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    static Dialog addressDialog;
    Dialog dialog;
    TextView receiver;
    TextView message;
    TextView tvAmount;
    ConstraintLayout amountCountainer;
    LottieAnimationView animationView;
    Dialog statusDialog;




    public DialogClass(Activity activity) {
        progressDialog = new Dialog(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCanceledOnTouchOutside(false);

        statusDialog = new Dialog(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        statusDialog.setContentView(R.layout.status_dialog);
        statusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        statusDialog.setCanceledOnTouchOutside(false);
        receiver = statusDialog.findViewById(R.id.tvReceiver);
        message = statusDialog.findViewById(R.id.tvMessage);
        tvAmount = statusDialog.findViewById(R.id.tvAmount);
        amountCountainer = statusDialog.findViewById(R.id.amountContainer);

        animationView = statusDialog.findViewById(R.id.animationViewStatus);

    }

    public void progressShow(String s){
        TextView textView  = progressDialog.findViewById(R.id.tvProgressMsg);
        textView.setText(s);
        progressDialog.show();
    }

    public void progresshide(){
        progressDialog.cancel();
    }


    public void hideInternetError() {
        statusDialog.hide();
    }

    public void showInternetError() {
        animationView.setAnimation(R.raw.nointernet);
        message.setVisibility(View.INVISIBLE);
        amountCountainer.setVisibility(View.GONE);
        statusDialog.setCancelable(false);
        receiver.setText("NO Internet Connectivity");
        animationView.resumeAnimation();
        statusDialog.show();

    }


//   ---------------------------------------------------------------------- Address Dailog -------------------------------------------------------------------------------------------------------


    public void editAddressDialog(Activity activity, AddressAdapter addressAdapter){

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.dialog_add_address, null);
        DialogAddAddressBinding binding = DialogAddAddressBinding.bind(view);

        addressDialog = new Dialog(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert);
        addressDialog.setContentView(binding.getRoot());
        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addressDialog.setCanceledOnTouchOutside(false);

        List<String> adr = new ArrayList<>();
        adr.addAll(HomeActivity.CurrentUserDetail.getAddress());
        AddressAdapter adapter = new AddressAdapter(adr, "EDIT");
        binding.rvAddressEdit.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL,false));
        binding.rvAddressEdit.setAdapter(adapter);
        addressAdapter.notifyDataSetChanged();

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(adr.size()>=3){
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.tvError.setText("Sorry you can't add more then 3 address");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvError.setVisibility(View.GONE);
                        }
                    },3000);
                    return;
                }

                String etAddress = binding.etAddress.getText().toString().trim();
                if(etAddress.length()>0){
                    adr.add(etAddress);
                    addressAdapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                    binding.etAddress.setText("");
                }else{
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.tvError.setText("Please enter valid address");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvError.setVisibility(View.GONE);
                        }
                    },3000);
                }
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adr.size()>0){
                    progressShow("Uploading Product Details..");
                    DocumentReference docRef = db.collection("users").document(mAuth.getUid());

                    docRef.update("address", adr)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progresshide();
                                    addressDialog.cancel();
                                    addressAdapter.notifyDataSetChanged();
                                    Toast.makeText(activity, "Address Update Successfully", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progresshide();
                                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                        binding.tvError.setText("Please add atlease one address");
                        binding.tvError.setVisibility(View.VISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvError.setVisibility(View.GONE);
                            }
                        },3000);
                }
            }
        });

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressDialog.cancel();
            }
        });


        addressDialog.show();






    }

    public void showMessageDialog(Activity activity,String message , String status){

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.dialog_message, null);
        DialogMessageBinding binding = DialogMessageBinding.bind(view);

        Dialog dialog = new Dialog(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        if(status.equals("success")){
            binding.tvMessage.setText(message);
            dialog.show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.cancel();
            }
        },3000);


    }

    public void showConfirmationDialog(Activity activity , String msg,String btnName, onClick onCLick){

        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.confirmation_dialog, null);
        ConfirmationDialogBinding binding = ConfirmationDialogBinding.bind(view);

        dialog = new Dialog(activity, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog);
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(layoutParams);
        binding.btnYes.setText(btnName);
        binding.tvMessage.setText(msg);
        binding.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                onCLick.yesClick();
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();

    }


}
