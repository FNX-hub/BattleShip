package it.tranigrillo.battleship.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import it.tranigrillo.battleship.R;

public class ChooseGameActivity extends AppCompatActivity {

    private static final int RESULT = 102;

    private class Holder implements View.OnClickListener {
        Button btnEasy;
        Button btnMedium;
        Button btnPlayerLocal;
        Button btnPlayerBluetooth;
        Button btnPlayerWIFI;
        Holder() {
            btnEasy = findViewById(R.id.btnEasy);
            btnEasy.setOnClickListener(this);
            btnMedium = findViewById(R.id.btnMedium);
            btnMedium.setOnClickListener(this);
            btnPlayerLocal = findViewById(R.id.btnPlayerLocal);
            btnPlayerLocal.setOnClickListener(this);
            btnPlayerBluetooth = findViewById(R.id.btnPlayerBluetooth);
            btnPlayerBluetooth.setOnClickListener(this);
            btnPlayerWIFI = findViewById(R.id.btnPlayerWIFI);
            btnPlayerWIFI.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent activityIntent = new Intent(ChooseGameActivity.this, DeployActivity.class);
            switch (v.getId()) {
                case R.id.btnEasy:
                    activityIntent.putExtra("mode", "easy");
                    break;
                case R.id.btnMedium:
                    activityIntent.putExtra("mode", "medium");
                    break;
                case R.id.btnPlayerLocal:
                    activityIntent.putExtra("mode", "local");
                    break;
                case R.id.btnPlayerBluetooth:
                    activityIntent.putExtra("mode", "bluetooth");
                    break;
                case R.id.btnPlayerWIFI:
                    activityIntent.putExtra("mode", "wifi");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }
            ChooseGameActivity.this.startActivityForResult(activityIntent, RESULT);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_game_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        new Holder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            case R.id.miHelp:
                activityIntent = new Intent(ChooseGameActivity.this, HelpActivity.class);
                startActivityForResult(activityIntent, RESULT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
