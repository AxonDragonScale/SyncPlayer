package com.sdpd.syncplayer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class HostListAdapter extends RecyclerView.Adapter<HostListAdapter.HostViewHolder> {


    @NonNull
    @Override
    public HostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HostViewHolder hostViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class HostViewHolder extends RecyclerView.ViewHolder {

        public HostViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
