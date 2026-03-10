package com.rushd.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

/**
 * Monitors network connectivity using the modern ConnectivityManager API.
 * Calls {@link NetworkCallback} on the network thread; callers must post
 * to the UI thread themselves (MainActivity does this).
 */
public class NetworkMonitor {

    public interface NetworkCallback {
        void onNetworkAvailable();
        void onNetworkLost();
    }

    private final ConnectivityManager connectivityManager;
    private final NetworkCallback callback;
    private ConnectivityManager.NetworkCallback networkCallback;

    public NetworkMonitor(Context context, NetworkCallback callback) {
        this.connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.callback = callback;
    }

    public void start() {
        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                callback.onNetworkAvailable();
            }

            @Override
            public void onLost(@NonNull Network network) {
                callback.onNetworkLost();
            }
        };

        connectivityManager.registerNetworkCallback(request, networkCallback);

        // Emit initial state immediately
        if (isConnected()) {
            callback.onNetworkAvailable();
        } else {
            callback.onNetworkLost();
        }
    }

    public void stop() {
        if (networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (IllegalArgumentException ignored) {
                // Already unregistered
            }
        }
    }

    public boolean isConnected() {
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) return false;
        NetworkCapabilities caps =
                connectivityManager.getNetworkCapabilities(activeNetwork);
        return caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
}
