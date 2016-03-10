package com.dynamiclist.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Utility Class to provide the Network information to handle scenarios accordingly
 * Created by Saravanan on 3/7/2016.
 */
public class NetworkConnection {
    @SuppressWarnings("CanBeFinal")
    private final Context mContext;

    @SuppressWarnings("unused")
    public NetworkConnection(Context context) {
        this.mContext = context;
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager networkConnectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (networkConnectivity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = networkConnectivity.getAllNetworks();
                NetworkInfo info;
                if (networks != null)
                    for (Network anInfo : networks) {
                        info = networkConnectivity.getNetworkInfo(anInfo);
                        if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                            return true;
                        }
                    }
            } else {
                NetworkInfo[] info = networkConnectivity.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }

            }
        }
        return false;
    }

}

