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

    ArrayList<String> clientNamesOnline;

    public ClientListAdapter() {
        clientNamesOnline = new ArrayList<String>();
        clientNamesOnline.add("Client1");
        clientNamesOnline.add("Client2");
        clientNamesOnline.add("Client3");
        clientNamesOnline.add("Client4");
        clientNamesOnline.add("Client5");
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.client_list_item, viewGroup, false);

        ClientViewHolder viewHolder = new ClientViewHolder(view);
        Log.d("CLIENT_VIEW_CREATE", "CREATE: " + Integer.toString(i));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder hostViewHolder, int i) {
        TextView textView = hostViewHolder.textView;
        textView.setText(clientNamesOnline.get(i));
        textView.setOnClickListener(hostViewHolder);
        Log.d("CLIENT_VIEW_CREATE", "BIND: " + Integer.toString(i));
    }

    @Override
    public int getItemCount() {
        return clientNamesOnline.size();
    }

    public class ClientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ConstraintLayout row;
        public TextView textView;
        public Button button;

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Log.i("CLIENT_VIEW_CREATE", "BUTTON_PRESS");
            Toast.makeText(view.getContext(), clientNamesOnline.get(pos), Toast.LENGTH_LONG).show();
        }

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            row = (ConstraintLayout) itemView.findViewById(R.id.host_list_row);
            textView = (TextView) itemView.findViewById(R.id.tv_item);
            button = (Button) itemView.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (button.getText().equals("Connect")) {
                        button.setText("Kick");
                    } else {
                        button.setText("Connect");
                    }
                }
            });
        }
    }
}
