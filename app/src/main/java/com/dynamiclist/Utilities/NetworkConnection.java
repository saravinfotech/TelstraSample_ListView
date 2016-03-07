package com.dynamiclist.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Saravanan on 3/7/2016.
 */
public class NetworkConnection {
    private Context _context;

    public NetworkConnection(Context context){
        this._context = context;
    }

    public boolean isConnectionAvailable(){
        ConnectivityManager networkConnectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (networkConnectivity != null)
        {
            NetworkInfo[] info = networkConnectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }
}

