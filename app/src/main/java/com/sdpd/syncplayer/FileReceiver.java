package com.sdpd.syncplayer;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class FileReceiver implements Runnable {
    Thread t;
    Socket sock;
    DataInputStream dis;
    BufferedInputStream bis;
    DataOutputStream dos;
    FileOutputStream fos;

    InetAddress IP;
    int port;

    FileReceiver(String IP, int port) {
        try {
            this.IP = InetAddress.getByName(IP);
            this.port = port;
            t = new Thread(this);
            t.start();
        } catch (UnknownHostException e) {
            Log.e("FILE_RECV", e.toString());
        }
    }

    FileReceiver(InetAddress IP, int port) {
        this.IP = IP;
        this.port = port;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        String fle = "xxx";
        try {
            sock = new Socket(IP, port);
            dis = new DataInputStream(sock.getInputStream());
            dos = new DataOutputStream(sock.getOutputStream());
            String filename = dis.readUTF();
            fle = filename.substring(filename.lastIndexOf("/")+1);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + fle);
            f.createNewFile();
            fos = new FileOutputStream(f);
            bis = new BufferedInputStream(sock.getInputStream());
        } catch (Exception e) {
            Log.e("FILE_RECV", e.toString());
        }
        Log.d("FILE_RECV", "Recieve_START");
        int read;
        int flen = 2048;
        byte[] arr = new byte[flen];
        try {
            while ((read = bis.read(arr, 0, flen)) != -1) {
                fos.write(arr, 0, read);
            }
        } catch (Exception e) {
            Log.e("FILE_RECV", e.toString());
        }
        try {
            fos.close();
        } catch (IOException e) {
            Log.e("FILE_RECV", e.toString());
        }

        Log.d("FILE_RECV", "Recieved " + fle);
    }
}
