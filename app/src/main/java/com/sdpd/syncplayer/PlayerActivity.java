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
import android.widget.Toast;

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

public class PlayerActivity extends AppCompatActivity {

    String TAG = "PlayerActivity";

    private SimpleExoPlayer player;
    private PlayerView pvExoplayer;

    private RecyclerView rvClientList;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FileSender fs;
    private String path;

    private SyncServer syncServer;
    private SyncClient syncClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        path = getIntent().getStringExtra(getString(R.string.mediaSelectPathExtra));
        File file = (File)getIntent().getSerializableExtra(getString(R.string.mediaSelectFileExtra));

        Log.e(TAG, "onCreate");

        // Remove action bar and status bar for proper fullscreen
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        player = ExoPlayerFactory.newSimpleInstance(this);
        pvExoplayer = findViewById(R.id.pv_exoplayer);
        pvExoplayer.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));

        // This is the MediaSource representing the media to be played.
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(file));

        // Prepare the player with the source.
        player.prepare(mediaSource);

        // Setup the entire recycler view for client list
        rvClientList = findViewById(R.id.rv_clientList);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ClientListAdapter();

        rvClientList.setHasFixedSize(true);
        rvClientList.setLayoutManager(layoutManager);
        rvClientList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            fs = new FileSender(path, 3078);
        }
        if (GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            syncServer = new SyncServer(this);
            player.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Toast.makeText(PlayerActivity.this, "PLAY: " + playWhenReady, Toast.LENGTH_SHORT).show();
                    syncServer.setPlayState(playWhenReady);
                }
            });
        } else if (GlobalData.deviceRole == GlobalData.DeviceRole.CLIENT) {
            InetAddress addr = (InetAddress)getIntent().getSerializableExtra("HOST");
            syncClient = new SyncClient(this, addr);
        }
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

        Toast.makeText(this, "STOP", Toast.LENGTH_SHORT).show();
        Log.d("PLAYER_ACTIVITY", "STOP");

        if (GlobalData.deviceRole == GlobalData.DeviceRole.HOST) {
            fs.harakiri();
            fs = null;
        }
        if (syncClient != null) syncClient.close();
        if (syncServer != null) syncServer.close();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }

    public long getPlaybackPosition() {
        return player.getCurrentPosition();
    }

    public void seekTo(long l) {
        player.seekTo(l);
    }

    public void setPlay(boolean b) {
        player.setPlayWhenReady(b);
    }
}
