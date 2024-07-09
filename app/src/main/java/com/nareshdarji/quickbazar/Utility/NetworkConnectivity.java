package com.nareshdarji.quickbazar.Utility;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkConnectivity extends BroadcastReceiver {

    DialogClass dialogClass;
    Activity activity;
    Context context;
    public NetworkConnectivity(Activity activity) {
        dialogClass = new DialogClass(activity);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if(intent.getAction().equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")){
            if(isOnline()){
                dialogClass.hideInternetError();
            }else{
                dialogClass.showInternetError();
            }
        }

    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }
}
