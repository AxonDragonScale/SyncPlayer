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
        String clientNick;

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

                // get the nick
                clientNick = dis.readUTF();
                playerActivity.adapter.addClient(clientNick);

                calcDelay();
                calcSeekDelay();
            } catch (IOException e) {
                Log.e("SYNC_SOCK", e.toString());
                close();
            }
        }

        public void calcDelay() {
            try {
                dos.writeInt(SyncCommand.ECHO_RTT.ordinal());
                dos.writeLong(currentTimeMillis());
                if (dis.readInt() == SyncCommand.ECHO_RTT.ordinal()) {
                    echoDelay = (currentTimeMillis() - dis.readLong()) / 2;
                } else {
                    Log.e("SYNC_SOCK", "protocol_not_followed");
                    dis.readLong();
                }
            } catch (IOException e) {
                Log.e("SYNC_SERV", e.toString());
            }
        }

        public void calcSeekDelay() {
            try {
                dos.writeInt(SyncCommand.ECHO_SEEK.ordinal());
                dos.writeLong(currentTimeMillis());
                if (dis.readInt() == SyncCommand.ECHO_SEEK.ordinal()) {
                    totalDelay = (currentTimeMillis() - dis.readLong()) - echoDelay;
                } else {
                    Log.e("SYNC_SOCK", "protocol_not_followed");
                    dis.readLong();
                }
            } catch (IOException e) {
                Log.e("SYNC_SERV", e.toString());
            }
        }

        public void syncToTime(long l) {
            if (socket != null && dos != null) {
                lock.lock();
                try {
                    dos.writeInt(SyncCommand.SYNC.ordinal());
                    dos.writeLong(l + echoDelay);
                } catch (IOException e) {
                    Log.e("SYNC_SOCK", e.toString());
                    socket = null;
                }
                lock.unlock();
            } else {
                close();
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
                close();
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
                close();
            }
        }

        public void kick() {
            if(socket != null && dos != null) {
                exec.execute(() -> {
                    lock.lock();
                    try {
                        dos.writeInt(SyncCommand.KICK.ordinal());
                        dos.writeLong(0);
                        close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    lock.unlock();
                });
            }
        }

        public boolean isNull() {
            return (socket == null);
        }

        public void close() {
            try {
                connectedSockets.remove(this);
                playerActivity.adapter.removeClient(clientNick);
                socket.close();
            } catch (IOException e) {
                Log.e("SYNC_SOCK", e.toString());
            }
            socket = null;
        }
    }

    private String TAG = "SYNC_SERV";

    private ArrayList<SyncSocket> connectedSockets;
    private Thread listeningThread;
    private Thread runningThread;

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
        listeningThread = new Thread(() -> {
            while(running.get()) {
                try {
                    Socket s = serv.accept();
                    SyncSocket syncSocket = new SyncSocket(s);
                    if (playState) {
                        syncSocket.play(playerActivity.getExactPlaybackPosition());
                    } else {
                        syncSocket.pause(playerActivity.getExactPlaybackPosition());
                    }
                    connectedSockets.add(syncSocket);
                } catch (IOException e) {
                    running.set(false);
                    Log.e(TAG, e.toString());
                }
            }
        });
        listeningThread.start();

        runningThread = new Thread(() -> {
            for (SyncSocket s : connectedSockets) {
                s.calcDelay();
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e("SYNC_SERV", e.toString());
            }
        });
    }

    public void kick(String clientNick) {
        for(SyncSocket s: connectedSockets) {
            if(s.clientNick == clientNick) {
                s.kick();
            }
        }
    }

    public void togglePlayState() {
        playState = !playState;
        sync();
    }

    public void sync() {
        if (playState) {
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
