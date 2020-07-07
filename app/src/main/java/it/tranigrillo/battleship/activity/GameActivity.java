package it.tranigrillo.battleship.activity;

import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.Converter;
import it.tranigrillo.battleship.model.Game;
import it.tranigrillo.battleship.model.ShipMatrix;
import it.tranigrillo.battleship.view.Board;
import it.tranigrillo.battleship.view.TextViewObserver;

public class GameActivity extends AppCompatActivity {

    private static final int RESULT = 104;
    Game game;
    ShipMatrix myShipMatrix;
    ShipMatrix enemyMatrix;

    private class Holder {
        TextView tvTime;
        Board brdMini;
        Board brdMaxi;

        Holder() {
            this.tvTime = findViewById(R.id.tvTime);
            this.brdMini = findViewById(R.id.brdMini);
            this.brdMaxi = findViewById(R.id.brdMaxi);

            List<TextView> list = new ArrayList<>();

            list.add((TextView) findViewById(R.id.tvSmallShipCounter));
            list.add((TextView) findViewById(R.id.tvMediumShipCounter));
            list.add((TextView) findViewById(R.id.tvLargeShipCounter));
            list.add((TextView) findViewById(R.id.tvExtraLargeShipCounter));


            TextViewObserver observer = new TextViewObserver(list);

            brdMini.setMyShipMatrix(myShipMatrix);
            brdMini.setGame(game, false, true);
            brdMaxi.setGame(game, false, false);
            brdMaxi.setTextViewObserver(observer);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        game = getIntent().getParcelableExtra("game");
        myShipMatrix = new Converter().matrixFromString(getIntent().getStringExtra("matrix"));
        enemyMatrix = new Converter().matrixFromString(getIntent().getStringExtra("matrix"));

        new Holder();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }
}
