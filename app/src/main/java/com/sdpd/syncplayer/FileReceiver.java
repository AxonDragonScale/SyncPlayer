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
import java.util.concurrent.Callable;

public class FileReceiver implements Runnable {
    Thread t;
    Socket sock;
    DataInputStream dis;
    BufferedInputStream bis;
    DataOutputStream dos;
    FileOutputStream fos;

    InetAddress IP;
    int port;

    boolean isPasswordCorrect;

    DownloadActivity downloadActivity;

    public FileReceiver(String IP, int port, DownloadActivity downloadActivity) {
        this.downloadActivity = downloadActivity;
        try {
            this.IP = InetAddress.getByName(IP);
            this.port = port;
            t = new Thread(this);
            t.start();
        } catch (UnknownHostException e) {
            Log.e("FILE_RECV", e.toString());
        }
    }

    public FileReceiver(InetAddress IP, int port, DownloadActivity downloadActivity) {
        this.downloadActivity = downloadActivity;
        this.IP = IP;
        this.port = port;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        String fle = "xxx";
        String filepath = "";
        File file = null;
        long filelen = 0;
        try {
            sock = new Socket(IP, port);
            dis = new DataInputStream(sock.getInputStream());
            dos = new DataOutputStream(sock.getOutputStream());

            String truePassword = dis.readUTF();
            isPasswordCorrect = downloadActivity.password.equals(truePassword);
            if(!isPasswordCorrect) {
                dos.writeBoolean(false);
                downloadActivity.onFailure("Password Incorrect");
            } else {
                dos.writeBoolean(true);
                String filename = dis.readUTF();
                filelen = dis.readLong();
                fle = filename.substring(filename.lastIndexOf("/") + 1);
                filepath = Environment.getExternalStorageDirectory() + File.separator + fle;
                file = new File(filepath);
                file.createNewFile();
                fos = new FileOutputStream(file);
                bis = new BufferedInputStream(sock.getInputStream());
            }
        } catch (Exception e) {
            Log.e("FILE_RECV", e.toString());
        }

        if(isPasswordCorrect) {
            Log.d("FILE_RECV", "Recieve_START");
            int read;
            int flen = 2048;
            long total_read = 0;
            byte[] arr = new byte[flen];
            try {
                while ((read = bis.read(arr, 0, flen)) != -1) {
                    total_read += read;
                    fos.write(arr, 0, read);
                    downloadActivity.setProgress((int) (total_read * 100 / filelen), ((total_read / 1024) / 1024), ((filelen / 1024) / 1024));
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

            if (!fle.equals("xxx") && total_read == filelen) {
                downloadActivity.onFinishDownload(filepath, file);
            } else {
                downloadActivity.onFailure("Host Disconnected.");
            }
        }
    }
}
