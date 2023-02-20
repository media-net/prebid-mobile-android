package com.app.analytics.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo

class NetworkStateBroadcastReceiver(private val listener: NetworkWatcher.NetworkConnectionListener) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager: ConnectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo: NetworkInfo? = connectivityManager.getActiveNetworkInfo()

        val isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting()

        // call listener method
        listener.onNetworkChange(isConnected)
    }
}
