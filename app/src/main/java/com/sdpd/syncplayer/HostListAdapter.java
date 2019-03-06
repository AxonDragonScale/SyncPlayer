package com.sdpd.syncplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HostListAdapter extends RecyclerView.Adapter<HostListAdapter.HostViewHolder> {

    ArrayList<String> hostNamesOnline;

    public HostListAdapter() {
        hostNamesOnline = new ArrayList<String>();
        hostNamesOnline.add("Host1");
        hostNamesOnline.add("Host2");
        hostNamesOnline.add("Host3");
        hostNamesOnline.add("Host4");
        hostNamesOnline.add("Host5");
    }

    @NonNull
    @Override
    public HostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.host_list_item, viewGroup, false);

        HostViewHolder viewHolder = new HostViewHolder(view);
        Log.d("HOST_VIEW_CREATE", "CREATE: " + Integer.toString(i));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HostViewHolder hostViewHolder, int i) {
        TextView textView = hostViewHolder.textView;
        textView.setText(hostNamesOnline.get(i));
        textView.setOnClickListener(hostViewHolder);
        Log.d("HOST_VIEW_CREATE", "BIND: " + Integer.toString(i));
    }

    @Override
    public int getItemCount() {
        return hostNamesOnline.size();
    }

    public class HostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ConstraintLayout row;
        public TextView textView;

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Log.i("HOST_VIEW_CREATE", "BUTTON_PRESS");
            Toast.makeText(view.getContext(), hostNamesOnline.get(pos), Toast.LENGTH_LONG).show();
        }

        public HostViewHolder(@NonNull View itemView) {
            super(itemView);
            row = (ConstraintLayout) itemView.findViewById(R.id.host_list_row);
            textView = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }
}
