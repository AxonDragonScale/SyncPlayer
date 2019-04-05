package com.sdpd.syncplayer;

import java.io.Serializable;
import java.net.InetAddress;

public class Host implements Serializable {
    public String hostName;
    public InetAddress hostAddress;
    public int hostPort;

    public Host(String hostName, InetAddress hostAddress, int hostPort) {
        this.hostAddress = hostAddress;
        this.hostName = hostName;
        this.hostPort = hostPort;
    }
}
