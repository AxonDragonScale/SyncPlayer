package com.sdpd.syncplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

public class HostActivity extends AppCompatActivity {

    Button btnPickMedia;
    Button btnOpenPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        btnPickMedia = findViewById(R.id.btn_pickMedia);
        btnOpenPlayer = findViewById(R.id.btn_openPlayer);

        // Choosing a media file
        btnPickMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooserDialog dialog = new ChooserDialog(HostActivity.this);
                dialog.displayPath(true);
                dialog.withFilter(false, "mp3", "wav", "mp4", "mkv", "webm");
                dialog.withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File file) {
                        Toast.makeText(HostActivity.this, "Path: " + path, Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.build().show();
            }
        });

        btnOpenPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HostActivity.this, PlayerActivity.class);
                // TODO: Add whatever needs to be sent to PlayerActivity in the intent

                startActivity(intent);
            }
        });


    }
}
