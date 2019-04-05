package com.sdpd.syncplayer;

import android.net.Uri;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

class FileSenderHandler implements Runnable {
    Socket sock;
    DataInputStream dis;
    DataOutputStream dos;
    FileInputStream fis;
    String filename;

    FileSenderHandler(Socket s, String filen) {
        sock = s;
        try {
            dis = new DataInputStream(sock.getInputStream());
            dos = new DataOutputStream(sock.getOutputStream());
            filename = filen;
            dos.writeUTF(filen);
        } catch (IOException e) {
            Log.e("FILE_SENDER_HANDLER", e.getMessage() + "\n" + e.getStackTrace());
        }
    }

    @Override
    public void run() {
        try {
            fis = new FileInputStream(filename);

            int read;
            int flen = 2048;
            byte[] arr = new byte[flen];
            int i = 0;
            while ((read = fis.read(arr, 0, flen)) != -1) {
                i++;
                Log.d("FILE_SENDER_PROG", Integer.toString(i));
                dos.write(arr, 0, read);
            }
            Thread.sleep(1);
        } catch (IOException e) {
            Log.e("FILE_SENDER_HANDLER", e.toString());
        } catch (InterruptedException e) {
            Log.e("FILE_SENDER_HANDLER", "Killed");
        }
        try {
            dis.close();
        } catch (IOException e) {
            Log.e("FILE_SENDER_HANDLER", e.toString());
        }
        try {
            dos.close();
        } catch (IOException e) {
            Log.e("FILE_SENDER_HANDLER", e.toString());
        }
        try {
            fis.close();
        } catch (IOException e) {
            Log.e("FILE_SENDER_HANDLER", e.toString());
        }
        try {
            sock.close();
        } catch (IOException e) {
            Log.e("FILE_SENDER_HANDLER", e.toString());
        }
    }
}

public class FileSender implements Runnable {
    ServerSocket sock;
    ExecutorService tpe;
    Thread t;
    final String filen;

    AtomicBoolean running;

    public FileSender(String filename, int port) {
        running = new AtomicBoolean(true);
        filen = filename;
        tpe = Executors.newCachedThreadPool();
        try {
            sock = new ServerSocket(port);
            t = new Thread(this);
            t.start();
        } catch (IOException e) {
            Log.e("FILE_SENDER", e.toString());
        }
    }

    synchronized void harakiri() {
        tpe.shutdownNow();
        running.set(false);
        try {
            sock.close();
        } catch (Exception e) {
            Log.e("FILE_SENDER", "HARAKIRI");
        }
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Log.d("FILE_SENDER", "Listening");
                tpe.execute(new FileSenderHandler(sock.accept(), filen));
            } catch (IOException e) {
                Log.e("FILE_SENDER", e.toString() + "1");
            }
        }
        try {
            sock.close();
        } catch (IOException e) {
            Log.e("FILE_SENDER", e.toString() + "2");
        }
    }
}
