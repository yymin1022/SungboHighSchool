package com.sungbospot.lunch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Tools {
    public static boolean isNetwork(Context mContext) {
        ConnectivityManager manager = (ConnectivityManager) mContext
			.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager
			.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager
			.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return wifi.isConnected() || mobile.isConnected();
    }
}
