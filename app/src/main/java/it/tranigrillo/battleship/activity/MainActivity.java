package it.tranigrillo.battleship.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import it.tranigrillo.battleship.R;

public class MainActivity extends AppCompatActivity {

    private static final int RESULT = 101;

    private class Holder implements View.OnClickListener {
        Button btnCustomPlay;
        Button btnProfile;
        Button btnGameHistory;
        Button btnOption;
        Button btnHelp;
        Holder() {
            btnCustomPlay = findViewById(R.id.btnCustomPlay);
            btnCustomPlay.setOnClickListener(this);
            btnProfile = findViewById(R.id.btnProfile);
            btnProfile.setOnClickListener(this);
            btnGameHistory = findViewById(R.id.btnGameHistory);
            btnGameHistory.setOnClickListener(this);
            btnOption = findViewById(R.id.btnOption);
            btnOption.setOnClickListener(this);
            btnHelp = findViewById(R.id.btnHelp);
            btnHelp.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent activityIntent;
            switch (v.getId()) {
                case R.id.btnCustomPlay:
                    activityIntent = new Intent(MainActivity.this, ChooseGameActivity.class);
                    break;
                case R.id.btnProfile:
                    activityIntent = new Intent(MainActivity.this, ProfileActivity.class);
                    break;
                case R.id.btnGameHistory:
                    activityIntent = new Intent(MainActivity.this, HistoryActivity.class);
                    break;
                case R.id.btnOption:
                    activityIntent = new Intent(MainActivity.this, OptionActivity.class);
                    break;
                case R.id.btnHelp:
                    activityIntent = new Intent(MainActivity.this, HelpActivity.class);
                    break;
                default:
                    return;
            }
            MainActivity.this.startActivityForResult(activityIntent, RESULT);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Holder();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit? ");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
}
