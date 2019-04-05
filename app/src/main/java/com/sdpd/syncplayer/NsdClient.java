package com.sdpd.syncplayer;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class NsdClient {

    String TAG = "NsdClient";

    String SERVICE_NAME = GlobalData.nick;
    String SERVICE_TYPE = "_http._tcp.";

    NsdManager manager;
    NsdManager.DiscoveryListener discoveryListener;
    NsdManager.ResolveListener resolveListener;

    HostListAdapter adapter;

    public NsdClient(Context context, HostListAdapter adapter) {
        this.adapter = adapter;

        manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        makeResolveListener();
        makeDiscoveryListener();
    }

    public void discoverServices() {
        manager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void stopDiscovery() {
        if(manager != null) {
            manager.stopServiceDiscovery(discoveryListener);
        }
    }

    public void makeDiscoveryListener() {
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "startDiscovery failed with error code -> " + errorCode);
                manager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.d(TAG, "stopDiscovery failed with error code -> " + errorCode);
                manager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "Discovery started");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "Discovery stopped");
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service found -> " + serviceInfo);
                Log.d(TAG, "Host -> " + serviceInfo.getHost() + " and Port -> " + serviceInfo.getPort());

                if(!serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
                    Log.d(TAG, "Unknown service type -> " + serviceInfo.getServiceType());
                } else if(serviceInfo.getServiceName().equals(SERVICE_NAME)) {
                    Log.d(TAG, "Own service -> " + serviceInfo.getServiceName());
                } else {
                    Log.d(TAG, "Host found -> " + serviceInfo.getServiceName());
                    manager.resolveService(serviceInfo, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service lost -> " + serviceInfo.getServiceName());
                adapter.removeHost(serviceInfo.getServiceName(), serviceInfo.getHost(), serviceInfo.getPort());
            }
        };
    }

    public void makeResolveListener() {
        resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "Resolve failed with error code: " + errorCode);
                Log.d(TAG, "Service Info -> " + serviceInfo);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service resolved -> " + serviceInfo);
                adapter.addHost(serviceInfo.getServiceName(), serviceInfo.getHost(), serviceInfo.getPort());
            }
        };
    }
}
