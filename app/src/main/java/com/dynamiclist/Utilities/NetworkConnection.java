package com.dynamiclist.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility Class to provide the Network information to handle scenarios accordingly
 * Created by Saravanan on 3/7/2016.
 */
public class NetworkConnection {
    @SuppressWarnings("CanBeFinal")
    private Context mContext;

    public NetworkConnection(Context context){
        this.mContext = context;
    }

    public boolean isConnectionAvailable(){
        ConnectivityManager networkConnectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (networkConnectivity != null)
        {
            @SuppressWarnings("deprecation")
            NetworkInfo[] info = networkConnectivity.getAllNetworkInfo();
            if (info != null)
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}

