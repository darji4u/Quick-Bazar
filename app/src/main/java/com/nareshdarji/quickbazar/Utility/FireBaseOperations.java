package com.nareshdarji.quickbazar.Utility;


import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FireBaseOperations {

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Activity activity;
    List<String> category;

    public FireBaseOperations(Activity activity){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        this.activity = activity;
    }


}
