package it.tranigrillo.battleship.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.BluetoothService;
import it.tranigrillo.battleship.model.GameRule;

public class MatchMakingActivity extends AppCompatActivity {

    public static final String TAG = "MatchmackingActivity";;
    private static final int STATE_CONNECTED = 3;

    private boolean host;
    private GameRule gameRule;
    private Intent serviceIntent;

    private RecyclerView rvPlayer;
    private PlayerAdapter adapter;

    private IntentFilter discoveryFilter;
    private List<BluetoothDevice> devices = new ArrayList<>();

//    private CountDownTimer refresher = new CountDownTimer(30*1000, 1000) {
//        @Override
//        public void onTick(long millisUntilFinished) {
////            Log.d("Timer", "i'm alive");
//        }
//        @Override
//        public void onFinish() {
//            Log.d("Timer", "i'm stopping");
//            devices.clear();
//            adapter.notifyDataSetChanged();
//            startService("scan", null, null);
//            start();
//        }
//    };

    //receiver per la discovery
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action == BluetoothAdapter.ACTION_DISCOVERY_STARTED) {
                //la discovery è iniziata
                Log.d(TAG, "discoveryReceiver: ACTION_DISCOVERY_STARTED");
            }
            if (action == BluetoothDevice.ACTION_FOUND) {
                //ho trovato qualcosa
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null) {
                    devices.add(device);
                    adapter.setDevices(devices);
                }
                Log.d(TAG, "discoveryReceiver2: " + device.getName() + ": " + device.getAddress());
            }
            if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                //la discovery è terminata
                Log.d(TAG, "discoveryReceiver: ACTION_DISCOVERY_FINISHED");
            }
            if (action == "status") {
                int status = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("status")));
                if (status == STATE_CONNECTED && host) {
                    startService("write", "message", "messaggio di prova host"/*gameRule.getRule()*/);
                }
                if (status == STATE_CONNECTED && !host) {
                    startService("write", "message", "messaggio di prova guest");
                }
            }
            if (action == "read") {
                Log.d(TAG, intent.getStringExtra("message"));
            }
        }
    };

    private class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerHolder> {
        private List<BluetoothDevice> devices = new ArrayList<>();

        @NonNull
        @Override
        public PlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.players_layout, parent, false);
            return new PlayerHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PlayerHolder holder, int position) {
            BluetoothDevice device = devices.get(position);
            holder.tvItemTitle.setText(device.getName());
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public void setDevices(List<BluetoothDevice> devices) {
            this.devices = devices;
            notifyDataSetChanged();
        }

        class PlayerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView tvItemTitle;
            private CardView cvPlayer;

            public PlayerHolder(@NonNull View itemView) {
                super(itemView);
                tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
                cvPlayer = itemView.findViewById(R.id.cvPlayer);
                cvPlayer.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                startService("connect_device", "device_address", devices.get(getAdapterPosition()).getAddress());
//                refresher.cancel();
            }
        }
    }

    private class Holder implements View.OnClickListener {
        Button btnRefresh;

        Holder(Context context) {
            rvPlayer = findViewById(R.id.rvHosts);
            btnRefresh = findViewById(R.id.btnRefresh);
            btnRefresh.setOnClickListener(this);
            adapter = new PlayerAdapter();
            rvPlayer.setLayoutManager(new LinearLayoutManager(context));
            rvPlayer.setAdapter(adapter);
        }

        @Override
        public void onClick(View v) {
//            refresher.cancel();
//            refresher.onFinish();
        }
    }

    public void startService(String service, String extraKey, String extraValue) {
        serviceIntent = new Intent(MatchMakingActivity.this, BluetoothService.class);
        serviceIntent.putExtra("service", service);
        if (extraKey != null && extraValue != null) {
            serviceIntent.putExtra(extraKey, extraValue);
        }
        startService(serviceIntent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matchmaking_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // register bluetooth reciver
        discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryFilter.addAction("status");
        discoveryFilter.addAction("read");
        registerReceiver(discoveryReceiver, discoveryFilter);


        //chek if host
        gameRule = getIntent().getParcelableExtra("game");
        if(gameRule != null) {
            gameRule.print();
            host = true;
        }
        else {
            host = false;
        }
        Log.d(TAG, String.valueOf(host));
        startService("listen_scan", null, null);
//        refresher.start();
        new Holder(this);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(discoveryReceiver);
        super.onPause();
    }

    @Override
    protected void onResume(){
        registerReceiver(discoveryReceiver, discoveryFilter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(discoveryReceiver);
        super.onDestroy();
    }
}
