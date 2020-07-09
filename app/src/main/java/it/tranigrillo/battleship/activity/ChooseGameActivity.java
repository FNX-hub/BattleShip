package it.tranigrillo.battleship.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.GameRule;

public class ChooseGameActivity extends AppCompatActivity /*implements ServiceConnection*/ {

    private static final int RESULT = 102;
    private static final String TAG = "ChooseGameActivity";
//    private BluetoothService service;
//    ConnectionManager connectionManager;
    private GameRule gameRule;

//    @Override
//    public void onServiceConnected(ComponentName name, IBinder service) {
//        this.service = ((BluetoothService.LocalBinder)service).getService();
//    }
//
//    @Override
//    public void onServiceDisconnected(ComponentName name) {
//        service = null;
//    }

    private class Holder implements View.OnClickListener {
        Button btnEasy;
        Button btnMedium;
        Button btnHostBluetooth;
        Button btnGuestBluetooth;
        RadioGroup rgRule;

        Holder() {
            btnEasy = findViewById(R.id.btnEasy);
            btnMedium = findViewById(R.id.btnMedium);
            btnHostBluetooth = findViewById(R.id.btnHostBluetooth);
            btnGuestBluetooth = findViewById(R.id.btnGuestBluetooth);
            rgRule = findViewById(R.id.rgRules);

            btnEasy.setOnClickListener(this);
            btnMedium.setOnClickListener(this);
            btnHostBluetooth.setOnClickListener(this);
            btnGuestBluetooth.setOnClickListener(this);
        }


        /**
         * Android deve controllare esplicitamente se il permesso è stato concesso
         * (perchè è un livello di sicurezza pericoloso)
         */
        private void checkPermissions() {
            int permissionCheck = ChooseGameActivity.this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += ChooseGameActivity.this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                ChooseGameActivity.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }


        @Override
        public void onClick(View v) {
            final Intent activityIntent = new Intent(ChooseGameActivity.this, DeployActivity.class);
            RadioButton br = findViewById(rgRule.getCheckedRadioButtonId());
            switch (v.getId()) {
                case R.id.btnEasy:
                    gameRule = new GameRule(getApplicationContext(),br.getText().toString(), 1);
                    break;
                case R.id.btnMedium:
                    gameRule = new GameRule(getApplicationContext(),br.getText().toString(), 2);
                    break;
                case R.id.btnHostBluetooth:
                    gameRule = new GameRule(getApplicationContext(),br.getText().toString(), 9);
                    Intent matchMaking = new Intent(ChooseGameActivity.this, MatchMakingActivity.class);
                    matchMaking.putExtra("game", gameRule);
                    checkPermissions();
                    BluetoothAdapter.getDefaultAdapter().enable();
                    startActivityForResult(matchMaking, RESULT);
                    return;
                case R.id.btnGuestBluetooth:
                    checkPermissions();
                    BluetoothAdapter.getDefaultAdapter().enable();
                    startActivityForResult(new Intent(ChooseGameActivity.this, MatchMakingActivity.class), RESULT);
                    return;
                default:
            }
            activityIntent.putExtra("game", gameRule);
            ChooseGameActivity.this.startActivityForResult(activityIntent, RESULT);
        }
    }
//    /**
//     * Android deve controllare esplicitamente se il permesso è stato concesso
//     * (perchè è un livello di sicurezza pericoloso)
//     */
//    private void checkPermissions() {
//        int permissionCheck = ChooseGameActivity.this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
//        permissionCheck += ChooseGameActivity.this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
//        if (permissionCheck != 0) {
//            ChooseGameActivity.this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
//        }
//    }
//
//    private void setVisibiliy(int mode){
//        try {
//            //il metodo è Private, può essere chiamato qui soltanto tramite Reflection
//            Method method = BluetoothAdapter.class.getMethod("setScanMode", int.class);
//            method.invoke(bluetoothAdapter, mode);
//        }
//        catch ( NoSuchMethodException |IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
//            Log.e(TAG, "Failed to turn on bluetooth device discoverability.", e);
//        }
//    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//        connectionManager = new ConnectionManager(this);
        new Holder();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Intent intent = new Intent(this, BluetoothService.class);
//        bindService(intent, this, BIND_AUTO_CREATE);
//    }

    @Override
    protected void onDestroy() {
//        connectionManager.destroy(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
