package com.sdpd.syncplayer;

import android.util.Log;

import com.google.android.exoplayer2.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.System.currentTimeMillis;

public class SyncServer {


    class SyncSocket {

        Socket socket;
        DataOutputStream dos;
        DataInputStream dis;
        Lock lock;
        long echoDelay;
        long totalDelay;

        public SyncSocket(Socket s) {
            socket = s;
            lock = new ReentrantLock();
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
                dos.writeInt(SyncCommand.ECHO_RTT.ordinal());
                dos.writeLong(currentTimeMillis());
                if (dis.readInt() == SyncCommand.ECHO_RTT.ordinal()) {
                    echoDelay = (currentTimeMillis() - dis.readLong())/2;
                } else {
                    Log.e("SYNC_SOCK", "protocol_not_followed");
                    dis.readLong();
                }
            } catch (IOException e) {
                Log.e("SYNC_SOCK", e.toString());
            }
        }

        public void calcDelay(PlayerActivity p) {
            if (socket != null && dos != null) {
                lock.lock();
                try {
                    dos.writeInt(SyncCommand.ECHO_SEEK.ordinal());
                    dos.writeLong(p.getPlaybackPosition());
                    if (dis.readInt() == SyncCommand.ECHO_SEEK.ordinal()) {
                        totalDelay = (p.getPlaybackPosition() - dis.readLong() - echoDelay);
                    } else {
                        Log.e("SYNC_SOCK", "protocol_not_followed");
                        dis.readLong();
                    }
                } catch (IOException e) {
                    Log.e("SYNC_SOCK", e.toString());
                    socket = null;
                }
                lock.unlock();
            } else {
                socket = null;
            }
            Log.i("SYNC_SOCK_DELAYS", "" + echoDelay + " " + totalDelay);
        }

        public void syncToTime(long l) {
            if (socket != null && dos != null) {
                lock.lock();
                try {
                    dos.writeInt(SyncCommand.SYNC.ordinal());
                    dos.writeLong(l + totalDelay);
                } catch (IOException e) {
                    Log.e("SYNC_SOCK", e.toString());
                    socket = null;
                }
                lock.unlock();
            } else {
                socket = null;
            }
        }

        public void play(long l) {
            if (socket != null && dos != null) {
                lock.lock();
                try {
                    dos.writeInt(SyncCommand.PLAY.ordinal());
                    dos.writeLong(l + totalDelay);
                } catch (IOException e) {
                    Log.e("SYNC_SOCK", e.toString());
                    socket = null;
                }
                lock.unlock();
            } else {
                socket = null;
            }
        }

        public void pause(long l) {
            if (socket != null && dos != null) {
                lock.lock();
                try {
                    dos.writeInt(SyncCommand.PAUSE.ordinal());
                    dos.writeLong(l + totalDelay);
                } catch (IOException e) {
                    Log.e("SYNC_SOCK", e.toString());
                    socket = null;
                }
                lock.unlock();
            } else {
                socket = null;
            }
        }

        public boolean isNull() {
            return (socket == null);
        }

        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("SYNC_SOCK", e.toString());
            }
            socket = null;
        }
    }

    class Listener implements Runnable {

        private AtomicBoolean running;

        public Listener() {
            running = new AtomicBoolean(true);
        }
        @Override
        public void run() {
            while(running.get()) {
                try {
                    Socket s = serv.accept();
                    SyncSocket sync = new SyncSocket(s);
                    sync.calcDelay(playerActivity);
                    if (playState) {
                        sync.play(playerActivity.getPlaybackPosition());
                    } else {
                        sync.play(playerActivity.getPlaybackPosition());
                    }
                    connectedSockets.add(sync);
                } catch (IOException e) {
                    running.set(false);
                    Log.e(TAG, e.toString());
                }
            }
        }
    }

    Runnable listener;

    private String TAG = "SYNC_SERV";

    private ArrayList<SyncSocket> connectedSockets;
    private Thread listeningThread;

    private PlayerActivity playerActivity;
    private ServerSocket serv;

    private AtomicBoolean running;

    private Executor exec;

    private boolean playState;

    public SyncServer(PlayerActivity activity) {
        playerActivity = activity;
        try {
            serv = new ServerSocket(1603);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        connectedSockets = new ArrayList<>();

        playState = false;
        exec = Executors.newSingleThreadExecutor();

        running = new AtomicBoolean(true);
        listener = new Listener();
        listeningThread = new Thread(listener);
        listeningThread.start();
    }

    public void setPlayState(boolean b) {
        playState = b;
        if (b) {
            long pos = playerActivity.getPlaybackPosition();
            exec.execute(() -> {
                for (SyncSocket s : connectedSockets) {
                    s.play(pos);
                }
            });
        } else {
            long pos = playerActivity.getPlaybackPosition();
            exec.execute(() -> {
                for (SyncSocket s : connectedSockets) {
                    s.pause(pos);
                }
            });
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public void close() {
        for (SyncSocket s: connectedSockets) {
            s.close();
        }
        try {
            serv.close();
        } catch (IOException e) {
            Log.e("SYNC_SERV", e.toString());
        }
    }
}
