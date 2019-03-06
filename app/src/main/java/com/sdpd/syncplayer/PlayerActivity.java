package com.sdpd.syncplayer;

import android.content.res.Configuration;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

public class PlayerActivity extends AppCompatActivity {

    SimpleExoPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String path = getIntent().getStringExtra(GlobalManager.HOST_SELECT_PATH);
        File file = (File)getIntent().getSerializableExtra(GlobalManager.HOST_SELECT_FILE);
        player = ExoPlayerFactory.newSimpleInstance(this);
        PlayerView pview = findViewById(R.id.xplayer);
        pview.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "@string/app_name"));
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(file));
        // Prepare the player with the source.
        player.prepare(videoSource);

        if (savedInstanceState != null) {
            player.setPlayWhenReady(savedInstanceState.getBoolean("PLAY_WHEN_READY"));
            player.seekTo(savedInstanceState.getLong("SEEK_TIME", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("SEEK_TIME", player.getCurrentPosition());
        outState.putBoolean("PLAY_WHEN_READY", player.getPlayWhenReady());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}
