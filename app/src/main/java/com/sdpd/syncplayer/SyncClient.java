package com.sdpd.syncplayer;

import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncClient implements Runnable {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private AtomicBoolean running;

    private PlayerActivity playerActivity;

    private Thread thr;

    private InetAddress addr;

    public SyncClient(PlayerActivity playerActivity, InetAddress addr) {
        this.addr = addr;
        running = new AtomicBoolean(true);
        this.playerActivity = playerActivity;
        thr = new Thread(this);
        thr.start();
    }

    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            Log.e("SYNC_CLIENT", e.toString());
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(addr, 1603);
        } catch (IOException e) {
            Log.e("SYNC_CLIENT", e.toString());
        }
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // Send nick to the server
            dos.writeUTF(GlobalData.nick);
        } catch (IOException e) {
            Log.e("SYNC_CLIENT", e.toString());
        }

        Semaphore sem = new Semaphore(1);
        while (running.get()) {
            try {
                int command = dis.readInt();
                long val = dis.readLong();

                if (command == SyncCommand.SYNC.ordinal()) {
                    playerActivity.runOnUiThread(() -> playerActivity.seekTo(val));
                } else if (command == SyncCommand.PLAY.ordinal()) {
                    playerActivity.runOnUiThread(() -> {
                        playerActivity.seekTo(val);
                        playerActivity.setPlay(true);
                    });
                } else if (command == SyncCommand.PAUSE.ordinal()) {
                    playerActivity.runOnUiThread(() -> {
                        playerActivity.seekTo(val);
                        playerActivity.setPlay(false);
                    });
                } else if (command == SyncCommand.ECHO_RTT.ordinal()) {
                    try {
                        dos.writeInt(command);
                        dos.writeLong(val);
                    } catch (IOException e) {
                        Log.e("SYNC_CLIENT", e.toString());
                    }
                } else if (command == SyncCommand.ECHO_SEEK.ordinal()) {
                    playerActivity.runOnUiThread(() -> {
                        try {
                            sem.acquire();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        playerActivity.seekTo(playerActivity.getPlaybackPosition());
                        sem.release();
                    });
                    try {
                        sem.acquire();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    dos.writeInt(command);
                    dos.writeLong(val);
                    sem.release();
                } else if(command == SyncCommand.KICK.ordinal()) {
                    running.set(false);
                    playerActivity.player.release();
                    Intent intent = new Intent(playerActivity, MainActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    playerActivity.startActivity(intent);
                }
            } catch (IOException e) {
                running.set(false);
            }
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
