package com.sdpd.syncplayer;

public class GlobalData {

    static String nick = "NoNick";
    static DeviceRole deviceRole;
    static String password;

    enum DeviceRole {
        HOST,
        CLIENT
    }
}
