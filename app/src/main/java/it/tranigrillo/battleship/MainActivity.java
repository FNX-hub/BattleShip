package it.tranigrillo.battleship;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        GridLayout glBoard = findViewById(R.id.glBoard);
        GridLayout glMini = findViewById(R.id.glMini);

        new Grid(glBoard, 10, new GridMatrix(getApplicationContext(), 10), true);

        GridMatrix matrix = new GridMatrix(getApplicationContext(), 10);
        matrix.addShip(0, 0, Fleet.SMALL, null);
        matrix.addShip(3, 3, Fleet.MEDIUM, null);
        matrix.addShip(6, 5, Fleet.LARGE, null);
        matrix.addShip(9, 7, Fleet.EXTRA_LARGE, null);
        new Grid(glMini, 10, matrix, false);

        }
 }
