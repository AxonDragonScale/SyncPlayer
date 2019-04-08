package com.sdpd.syncplayer;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ClientViewHolder> {

    ArrayList<String> clientNicks;

    PlayerActivity playerActivity;

    public ClientListAdapter(PlayerActivity playerActivity) {
        this.playerActivity = playerActivity;

        clientNicks = new ArrayList<String>();
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.client_list_item, viewGroup, false);

        Log.d("CLIENT_VIEW_CREATE", "CREATE: " + Integer.toString(i));
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder hostViewHolder, int i) {
        TextView textView = hostViewHolder.textView;
        textView.setText(clientNicks.get(i));
        textView.setOnClickListener(hostViewHolder);
        Log.d("CLIENT_VIEW_CREATE", "BIND: " + Integer.toString(i));
    }

    @Override
    public int getItemCount() {
        return clientNicks.size();
    }

    public void addClient(String clientNick) {
        clientNicks.add(clientNick);
        updateUI();
    }

    public void removeClient(String clientNick) {
        for(int i = 0; i<clientNicks.size(); i++) {
            if(clientNicks.get(i).equals(clientNick)) {
                clientNicks.remove(i);
                break;
            }
        }
    }

    public void updateUI() {
        playerActivity.runOnUiThread(() -> notifyDataSetChanged());
    }

    public class ClientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ConstraintLayout row;
        public TextView textView;
        public Button button;

        @Override
        public void onClick(View view) {
            Log.i("CLIENT_VIEW_CREATE", "BUTTON_PRESS");
            Toast.makeText(view.getContext(), clientNicks.get(getAdapterPosition()), Toast.LENGTH_LONG).show();
        }

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            row = itemView.findViewById(R.id.host_list_row);
            textView = itemView.findViewById(R.id.tv_item);
            button = itemView.findViewById(R.id.button);
            button.setOnClickListener(view -> {
                playerActivity.syncServer.kick(clientNicks.get(getAdapterPosition()));
            });
        }
    }
}
