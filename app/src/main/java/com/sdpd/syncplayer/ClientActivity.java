package com.sdpd.syncplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class ClientActivity extends AppCompatActivity {

    RecyclerView rvHostList;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        rvHostList = findViewById(R.id.rv_hostList);
        layoutManager = new LinearLayoutManager(this);
        adapter = new HostListAdapter();

        rvHostList.setHasFixedSize(true);
        rvHostList.setLayoutManager(layoutManager);
        rvHostList.setAdapter(adapter);
    }
}
