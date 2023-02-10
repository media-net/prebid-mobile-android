package com.app.analytics.utils

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import com.app.logger.CustomLogger
import java.lang.ref.WeakReference

object NetworkWatcher {

    private val TAG = NetworkWatcher::class.java.simpleName
    private val listeners: MutableList<NetworkConnectionListener> = mutableListOf()

    private var contextReference: WeakReference<Context>? = null
    private var connectivityManager: ConnectivityManager? = null
    private var networkRequest: NetworkRequest? = null
    private var connectivityManagerCallback: ConnectivityManager.NetworkCallback? = null
    private var networkStateListenerBR: NetworkStateBroadcastReceiver? = null
    private var networkStateIntentFilter: IntentFilter? = null

    fun init(context: Context) {
        CustomLogger.debug(TAG, "init")
        contextReference = WeakReference(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contextReference?.get()?.let { initConnectivityManagerCallback(it) }
        } else {
            initNetworkMonitorBR()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initConnectivityManagerCallback(context: Context) {
        CustomLogger.debug(TAG, "initialising connectivity manager")
        connectivityManager = context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
            // network is available for use
            override fun onAvailable(network: Network) {
                sendUpdatesToListener(true)
                CustomLogger.debug(TAG, "Network available now")
            }

            // lost network connection
            override fun onLost(network: Network) {
                sendUpdatesToListener(false)
                CustomLogger.debug(TAG, "Network lost")
            }
        }
    }

    private fun initNetworkMonitorBR() {
        CustomLogger.debug(TAG, "initialising network BR")
        networkStateListenerBR = NetworkStateBroadcastReceiver(object : NetworkConnectionListener {
            override fun onNetworkChange(isConnected: Boolean) {
                sendUpdatesToListener(isConnected)
            }
        })
        networkStateIntentFilter = IntentFilter().apply {
            addAction("android.new.conn.CONNECTIVITY_CHANGE")
        }
    }

    private fun sendUpdatesToListener(isConnected: Boolean) {
        listeners.forEach {
            it.onNetworkChange(isConnected)
        }
    }

    private fun startNetworkMonitor() {
        CustomLogger.debug(TAG, "startNetworkMonitor()")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            networkRequest?.let { request ->
                connectivityManagerCallback?.let { callback ->
                    connectivityManager?.registerNetworkCallback(request, callback)
                }
            }
        } else {
            contextReference?.get()?.registerReceiver(networkStateListenerBR, networkStateIntentFilter)
        }
    }

    private fun stopNetworkMonitor() {
        CustomLogger.debug(TAG, "stopNetworkMonitor()")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManagerCallback?.let { connectivityManager?.unregisterNetworkCallback(it) }
        } else {
            contextReference?.get()?.unregisterReceiver(networkStateListenerBR)
        }
    }

    fun isConnectedToInternet(): Boolean {
        val command = "ping -c 1 google.com"
        var isConnected = false
        try {
            isConnected = Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: Exception) { }

        return isConnected
    }

    fun startListening(listener: NetworkConnectionListener) {
        CustomLogger.debug(TAG, "startListening(), listener: $listener")
        synchronized(this) {
            if (listeners.isEmpty()) {
                startNetworkMonitor()
            }
            listeners.add(listener)
        }
    }

    fun stopListening(listener: NetworkConnectionListener) {
        CustomLogger.debug(TAG, "stopListening(), listener: $listener")
        synchronized(this) {
            listeners.remove(listener)
            if (listeners.isEmpty()) {
                stopNetworkMonitor()
            }
        }
    }

    fun stop() {
        listeners.clear()
        contextReference?.clear()
        connectivityManager = null
        networkRequest = null
        connectivityManagerCallback = null
        networkStateListenerBR = null
        networkStateIntentFilter = null
    }

    interface NetworkConnectionListener {
        fun onNetworkChange(isConnected: Boolean)
    }
}
