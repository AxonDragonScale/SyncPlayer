package com.sdpd.syncplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

public class HostActivity extends AppCompatActivity {

    Button btnPickMedia;
    TextView tvChoosePasswordText;
    EditText etNewPassword;
    Button btnStartHosting;

    String lastSelectedMediaPath;
    File lastSelectedMediaFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        GlobalData.deviceRole = GlobalData.DeviceRole.HOST;

        initViews();
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
    }

    void initViews() {
        btnPickMedia = findViewById(R.id.btn_pickMedia);
        tvChoosePasswordText = findViewById(R.id.tv_choosePasswordText);
        etNewPassword = findViewById(R.id.et_newPassword);
        btnStartHosting = findViewById(R.id.btn_startHosting);

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
                        lastSelectedMediaPath = path;
                        lastSelectedMediaFile = file;

                        Toast.makeText(HostActivity.this, "Path: " + path, Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.build().show();
            }
        });

        btnStartHosting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etNewPassword.getText().toString();
                GlobalData.password = password;

                if(lastSelectedMediaFile != null && lastSelectedMediaPath != null) {
                    Intent intent = new Intent(HostActivity.this, PlayerActivity.class);
                    intent.putExtra(getString(R.string.mediaSelectPathExtra), lastSelectedMediaPath);
                    intent.putExtra(getString(R.string.mediaSelectFileExtra), lastSelectedMediaFile);

                    startActivity(intent);
                } else {
                    Toast.makeText(HostActivity.this, "Choose a File", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
}


