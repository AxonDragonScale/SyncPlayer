package com.sdpd.syncplayer;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class NsdHost {
    String TAG = "NsdHostTAG";

    String SERVICE_NAME = GlobalData.nick;
    String SERVICE_TYPE = "_http._tcp.";

    NsdManager manager;
    NsdManager.RegistrationListener registrationListener;

    int port = 7830;

    public NsdHost(Context context) {


        manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        makeRegistrationListener();
    }

    public void registerService() {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        manager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
    }

    public void unRegisterService() {
        if(manager != null) {
            manager.unregisterService(registrationListener);
        }
    }

    public void makeRegistrationListener() {
        registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "Service registration failed");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "Service unregistration failed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service registered successfully");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service unregistered successfully");
            }
        };
    }


}
