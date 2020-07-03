package it.tranigrillo.battleship.activity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.List;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.Matrix;
import it.tranigrillo.battleship.model.MatrixStatus;
import it.tranigrillo.battleship.model.Ship;

public class GameActivity extends AppCompatActivity {

    private final static int RESULT = 104;

    private Matrix retrive(Context context) {
        Gson gson = new Gson();
        List fleet = gson.fromJson(getIntent().getStringExtra("fleet"), List.class);
        MatrixStatus[][] statuses = gson.fromJson(getIntent().getStringExtra("status"), MatrixStatus[][].class);
        return new Matrix(context, statuses, (List<Ship>) fleet).print();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
        retrive(getApplicationContext());
    }
}
