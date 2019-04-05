package com.sdpd.syncplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadActivity extends AppCompatActivity {

    ProgressBar pbDownloadProgress;
    TextView tvDownloadProgess;

    FileReceiver fileReceiver;
    Host host;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        pbDownloadProgress = findViewById(R.id.pb_downloadProgess);
        tvDownloadProgess = findViewById(R.id.tv_downloadProgess);

        Intent intent = getIntent();
        host = (Host) intent.getSerializableExtra("hostKey");
        fileReceiver = new FileReceiver(host.hostAddress,3078);
    }

    public void setProgess(int progessInPercent, int downloaded, int totalSize) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbDownloadProgress.setProgress(progessInPercent);
                tvDownloadProgess.setText("" + downloaded + "MB/" + totalSize + "MB");
            }
        });
    }
}
