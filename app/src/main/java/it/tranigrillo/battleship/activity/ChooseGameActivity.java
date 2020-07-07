package it.tranigrillo.battleship.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.BluetoothService;
import it.tranigrillo.battleship.model.Game;

public class ChooseGameActivity extends AppCompatActivity {

    private static final int RESULT = 102;
    private static final String TAG = "ChooseGameActivity";
    private BluetoothAdapter adapter;
    private BluetoothService connectionService;
    private Game game;


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
                if (device.getName().equals("battleship")){
                    connectionService.startClient(device);
                }
            }
            if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                //la discovery è terminata
                Log.d(TAG, "discoveryReceiver: ACTION_DISCOVERY_FINISHED");;
            }

            if (action == BluetoothDevice.ACTION_ACL_CONNECTED) {
                Intent activityIntent = new Intent(ChooseGameActivity.this, DeployActivity.class);
                activityIntent.putExtra("game", game);
                ChooseGameActivity.this.startActivityForResult(activityIntent, RESULT);
            }
        }
    };




    private class Holder implements View.OnClickListener {
        Button btnEasy;
        Button btnMedium;
        Button btnPlayerLocal;
        Button btnPlayerBluetooth;
        RadioGroup rgRule;

        Holder() {
            btnEasy = findViewById(R.id.btnEasy);
            btnMedium = findViewById(R.id.btnMedium);
            btnPlayerLocal = findViewById(R.id.btnPlayerLocal);
            btnPlayerBluetooth = findViewById(R.id.btnPlayerBluetooth);
            rgRule = findViewById(R.id.rgRules);

            btnEasy.setOnClickListener(this);
            btnMedium.setOnClickListener(this);
            btnPlayerLocal.setOnClickListener(this);
            btnPlayerBluetooth.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent activityIntent = new Intent(ChooseGameActivity.this, DeployActivity.class);
            RadioButton br = findViewById(rgRule.getCheckedRadioButtonId());
            switch (v.getId()) {
                case R.id.btnEasy:
                    game = new Game(getApplicationContext(),br.getText().toString(), 1);
                    break;
                case R.id.btnMedium:
                    game = new Game(getApplicationContext(),br.getText().toString(), 2);
                    break;
                case R.id.btnPlayerLocal:
                    game = new Game(getApplicationContext(),br.getText().toString(), 8);
                    break;
                case R.id.btnPlayerBluetooth:
                    game = new Game(getApplicationContext(),br.getText().toString(), 9);
                    adapter.startDiscovery();
                    return;
                default:
            }
            activityIntent.putExtra("game", game);
            ChooseGameActivity.this.startActivityForResult(activityIntent, RESULT);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //Connessione
        adapter = BluetoothAdapter.getDefaultAdapter();
        adapter.enable();
        adapter.setName("battleship");
        connectionService = new BluetoothService(this, adapter);
        IntentFilter discoveryFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(discoveryReceiver, discoveryFilter);
        new Holder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.removeItem(R.id.miProfile);
        menu.removeItem(R.id.miLoogbook);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent activityIntent;
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.miOption:
                activityIntent = new Intent(ChooseGameActivity.this, OptionActivity.class);
                startActivityForResult(activityIntent, RESULT);
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
