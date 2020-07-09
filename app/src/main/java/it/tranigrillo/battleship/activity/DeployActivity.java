package it.tranigrillo.battleship.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.Converter;
import it.tranigrillo.battleship.model.GameRule;
import it.tranigrillo.battleship.view.Board;
import it.tranigrillo.battleship.view.TextViewObserver;

public class DeployActivity extends AppCompatActivity {

    private static final int RESULT = 103;
    GameRule gameRule;

    private class Holder implements View.OnClickListener {
        Button btnConfirm;
        Button btnAuto;
        Board brdDeploy;

        Holder() {

            btnConfirm = findViewById(R.id.btnConfirm);
            btnAuto = findViewById(R.id.btnAuto);
            brdDeploy = findViewById(R.id.brdDeploy);

            btnConfirm.setOnClickListener(this);
            btnAuto.setOnClickListener(this);

            brdDeploy.setGame(gameRule, true);
//            brdDeploy.setMyShipMatrix(new ShipMatrix(10));

            List<TextView> list = new ArrayList<>();
            list.add((TextView) findViewById(R.id.tvSmallShipCounter));
            list.add((TextView) findViewById(R.id.tvMediumShipCounter));
            list.add((TextView) findViewById(R.id.tvLargeShipCounter));
            list.add((TextView) findViewById(R.id.tvExtraLargeShipCounter));

            TextViewObserver observer = new TextViewObserver(list);
            brdDeploy.setTextViewObserver(observer);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnAuto) {
                brdDeploy.autoDisposition();
                brdDeploy.getLogicMatrix().print();
            }
            else {
                brdDeploy.confirmDisposition();
                Intent activityIntent = new Intent(DeployActivity.this, GameActivity.class);
                activityIntent.putExtra("game", gameRule);
                activityIntent.putExtra("matrix", new Converter().fromArrayList(brdDeploy.getLogicMatrix()));
                brdDeploy.getLogicMatrix().print();
                startActivityForResult(activityIntent, RESULT);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deploy_layout);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        gameRule = getIntent().getParcelableExtra("game");
        new Holder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.miQuit:
                finish(); //TODO funzione che stoppa il gioco
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
