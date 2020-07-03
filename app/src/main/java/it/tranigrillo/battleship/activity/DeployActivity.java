package it.tranigrillo.battleship.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.Objects;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.Matrix;
import it.tranigrillo.battleship.view.Board;

public class DeployActivity extends AppCompatActivity {

    private final static int RESULT = 103;

    private class Holder implements View.OnClickListener {
        Button btnAuto;
        Button btnConfirm;
        Board board;
        Matrix matrix;

        Holder(Context context) {
            btnAuto = findViewById(R.id.btnAuto);
            btnAuto.setOnClickListener(this);
            btnConfirm = findViewById(R.id.btnConfirm);
            btnConfirm.setOnClickListener(this);
            board = findViewById(R.id.board);
            matrix = new Matrix(context, 10);
            board.setMatrix(matrix);
            board.setDeploy(true);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnConfirm:
                    Intent activityIntent = new Intent(DeployActivity.this, GameActivity.class);
                    Gson gson = new Gson();
                    String fleet = gson.toJson(matrix.getFleet());
                    String status = gson.toJson(matrix.getStatus());
                    activityIntent.putExtra("fleet", fleet);
                    activityIntent.putExtra("status", status);
                    startActivityForResult(activityIntent, RESULT);
                    break;
                case R.id.btnAuto:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deploy_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        new Holder(this);
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
                activityIntent = new Intent(DeployActivity.this, OptionActivity.class);
                startActivityForResult(activityIntent, RESULT);
                return true;
            case R.id.miHelp:
                activityIntent = new Intent(DeployActivity.this, HelpActivity.class);
                startActivityForResult(activityIntent, RESULT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
