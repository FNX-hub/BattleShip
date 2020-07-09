package it.tranigrillo.battleship.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.Converter;
import it.tranigrillo.battleship.model.GameRule;
import it.tranigrillo.battleship.model.IAmanager;
import it.tranigrillo.battleship.model.MatrixStatus;
import it.tranigrillo.battleship.model.ShipMatrix;
import it.tranigrillo.battleship.model.ShipOrientation;
import it.tranigrillo.battleship.view.Board;
import it.tranigrillo.battleship.view.Box;
import it.tranigrillo.battleship.view.TextViewObserver;
import it.tranigrillo.battleship.view.TurnManager;

public class GameActivity extends AppCompatActivity  {

    private static final int RESULT = 104;
    GameRule gameRule;
    ShipMatrix myShipMatrix;
    ShipMatrix enemyMatrix;
    int turn=1;
    IAmanager opponent;
    Boolean isMyTurn;

    private class Holder implements View.OnClickListener{
        TextView tvTime;
        Board brdMini;
        Board brdMaxi;
        TurnManager turnManager;

        Holder() {
            this.tvTime = findViewById(R.id.tvTime);
            this.brdMini = findViewById(R.id.brdMini);
            this.brdMaxi = findViewById(R.id.brdMaxi);

            List<TextView> list = new ArrayList<>();

            list.add((TextView) findViewById(R.id.tvSmallShipCounter));
            list.add((TextView) findViewById(R.id.tvMediumShipCounter));
            list.add((TextView) findViewById(R.id.tvLargeShipCounter));
            list.add((TextView) findViewById(R.id.tvExtraLargeShipCounter));

            //inizializza observer - aggiorna i contatori delle navi
            TextViewObserver observer = new TextViewObserver(list);


            brdMini.setLogicMatrix(myShipMatrix);
            brdMini.setGame(gameRule, false);
            brdMini.setRadar();
            brdMaxi.setGame(gameRule, false);
            brdMaxi.setTextViewObserver(observer); //aggiorna i contatori delle navi

            brdMaxi.setClickListener(this);

            turnManager = new TurnManager(brdMini,brdMaxi, gameRule);
            isMyTurn = true;
        }

        @Override
        public void onClick(View v) {
            Box box = (Box)v;
            Integer[] lastMove;
            Integer[] startPos = new Integer[2];
            String result;
            Integer sankShipLen=0;
            ShipOrientation orientation=null;
            MatrixStatus myMatrixStatus;
            if(isMyTurn){

                //il Player clicca la cella
                lastMove = (Integer[]) brdMaxi.getBoxIndex().get(box);
                Log.d("GAME", "TURNO DEL GIOCATORE "+lastMove[0] + "," + lastMove[1]);

                turnManager.playerTurn(lastMove,box);

                //cambio del turno
                isMyTurn = !isMyTurn;
                turn++;

                Log.d("GAME", "TURNO DELL'IA ");
                turnManager.iaTurn();

                //cambio del turno
                isMyTurn = !isMyTurn;
                turn++;
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.notYourTurn, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startIAlv1()
    {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        //ottieni dalla precedente activity le regole della partita (lv IA - numero navi - tipo di partita)
        gameRule = getIntent().getParcelableExtra("game");

        //ottieni dalla precedente activity la disposizione delle tue navi
        myShipMatrix = new Converter().matrixFromString(getIntent().getStringExtra("matrix"));

        new Holder();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }
}
