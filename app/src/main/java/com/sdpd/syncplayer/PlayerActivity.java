package com.sdpd.syncplayer;

import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Time;

public class PlayerActivity extends AppCompatActivity {

    String TAG = "PlayerActivity";

    SimpleExoPlayer player;
    PlayerView pvExoplayer;

    private RecyclerView rvClientList;
    ClientListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private NsdHost nsdHost;

    private FileSender fs;
    private String path;
    private File file;

    SyncServer syncServer;
    private SyncClient syncClient;

    private long playbackPosition;

    SeekBar seekBar;
    TextView tvCurTime;
    TextView tvTotalTime;
    private Thread seekBarSyncThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Log.e(TAG, "onCreate");

        path = getIntent().getStringExtra(getString(R.string.mediaSelectPathExtra));
        file = (File)getIntent().getSerializableExtra(getString(R.string.mediaSelectFileExtra));

        ImageButton b = findViewById(R.id.btn_playPause);
        b.setOnClickListener(view -> {
            syncServer.togglePlayState();
        });

        initPlayer();
        initRvClientList();

        if (GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            nsdHost = new NsdHost(getApplicationContext());    // why not accepting context parameter
            nsdHost.registerService();

            fs = new FileSender(path, 3078);

            syncServer = new SyncServer(this);
//            player.addListener(new Player.EventListener() {
//                @Override
//                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                    Toast.makeText(PlayerActivity.this, "PLAY: " + playWhenReady, Toast.LENGTH_SHORT).show();
//                    syncServer.setPlayState(playWhenReady);
//                }
//            });
            try {
                InetAddress addr = InetAddress.getByName("127.0.0.1");
                syncClient = new SyncClient(this, addr);
            } catch (UnknownHostException e) {
                Log.e(TAG, e.toString());
            }
        } else if (GlobalData.deviceRole == GlobalData.DeviceRole.CLIENT) {
            InetAddress address = (InetAddress) getIntent().getSerializableExtra("HOST");
            syncClient = new SyncClient(this, address);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Destroy", Toast.LENGTH_SHORT).show();
        Log.d("PLAYER_ACTIVITY", "Destroy");

        if (GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            nsdHost.unRegisterService();
            fs.harakiri();
            fs = null;
        }

        if (syncClient != null) syncClient.close();
        if (syncServer != null) syncServer.close();

        player.release();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Log.e(TAG, "Config Changed");

        int currentOrientation = getResources().getConfiguration().orientation;
        ViewGroup.LayoutParams params = pvExoplayer.getLayoutParams();
        if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            rvClientList.setVisibility(View.GONE);
            params.height = params.MATCH_PARENT;
            pvExoplayer.setLayoutParams(params);

            // remove status bar
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if(currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            rvClientList.setVisibility(View.VISIBLE);
            params.height = 270 * getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;
            pvExoplayer.setLayoutParams(params);

            // show status bar
            // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        outState.putLong("SEEK_TIME", player.getCurrentPosition());
//        outState.putBoolean("PLAY_WHEN_READY", player.getPlayWhenReady());
    }

    public void initPlayer() {
        // Remove action bar and status bar for proper fullscreen
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        player = ExoPlayerFactory.newSimpleInstance(this);
        pvExoplayer = findViewById(R.id.pv_exoplayer);
        pvExoplayer.setPlayer(player);
        pvExoplayer.setUseController(false);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));

        // This is the MediaSource representing the media to be played.
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(file));

        // Prepare the player with the source.
        player.prepare(mediaSource);

        tvCurTime = findViewById(R.id.tv_curTime);
        tvTotalTime = findViewById(R.id.tv_totalTime);
        seekBar = findViewById(R.id.sb_seekbar);

        if(GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    long duration = player.getDuration();
                    playbackPosition = i * duration / 100;

                    if (b) {
                        syncServer.sync();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        seekBarSyncThread = new Thread(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Log.e("PLAYER", e.toString());
            }
            while (syncClient.isRunning()) {
                seekBar.setProgress((int) player.getCurrentPosition() * 100 / (int) player.getDuration());
                runOnUiThread(() -> {
                    long duration = player.getDuration();
                    long pbp = getExactPlaybackPosition();
                    String totalTime = String.format("%02d:%02d:%02d", (duration/3600000) % 24, (duration/60000) % 60, (duration/1000) % 60);
                    String curTime = String.format("%02d:%02d:%02d", (pbp/3600000) % 24, (pbp/60000) % 60, (pbp/1000) % 60);
                    tvTotalTime.setText(totalTime);
                    tvCurTime.setText(curTime);
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    Log.e("PLAYER", e.toString());
                }
            }
        });
        seekBarSyncThread.start();
    }

    public void initRvClientList() {
        if(GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            // Setup the entire recycler view for client list
            rvClientList = findViewById(R.id.rv_clientList);
            layoutManager = new LinearLayoutManager(this);
            adapter = new ClientListAdapter(this);

            rvClientList.setHasFixedSize(true);
            rvClientList.setLayoutManager(layoutManager);
            rvClientList.setAdapter(adapter);
        }
    }

    public long getPlaybackPosition() {
        return playbackPosition;
    }

    public long getExactPlaybackPosition() {
        return player.getCurrentPosition();
    }

    public void seekTo(long l) {
        player.seekTo(l);
    }

    public void setPlay(boolean b) {
        player.setPlayWhenReady(b);
    }
}
