package it.tranigrillo.battleship.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import it.tranigrillo.battleship.R;
import it.tranigrillo.battleship.model.ShipOrientation;

public class Row extends LinearLayout {
    private List<Box> scvList = new ArrayList<>();
    private List<TextView> tvList = new ArrayList<>();

//    ----------------------
//    CONSTRUCTORS
//    ----------------------

    public Row(Context context) {
        super(context);
        init(context);
    }

    public Row(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Row(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.row_layout, this);

        scvList.add((Box) findViewById(R.id.scv0));
        tvList.add((TextView) findViewById(R.id.tv0));

        scvList.add((Box) findViewById(R.id.scv1));
        tvList.add((TextView) findViewById(R.id.tv1));

        scvList.add((Box) findViewById(R.id.scv2));
        tvList.add((TextView) findViewById(R.id.tv2));

        scvList.add((Box) findViewById(R.id.scv3));
        tvList.add((TextView) findViewById(R.id.tv3));

        scvList.add((Box) findViewById(R.id.scv4));
        tvList.add((TextView) findViewById(R.id.tv4));

        scvList.add((Box) findViewById(R.id.scv5));
        tvList.add((TextView) findViewById(R.id.tv5));

        scvList.add((Box) findViewById(R.id.scv6));
        tvList.add((TextView) findViewById(R.id.tv6));

        scvList.add((Box) findViewById(R.id.scv7));
        tvList.add((TextView) findViewById(R.id.tv7));

        scvList.add((Box) findViewById(R.id.scv8));
        tvList.add((TextView) findViewById(R.id.tv8));

        scvList.add((Box) findViewById(R.id.scv9));
        tvList.add((TextView) findViewById(R.id.tv9));

        scvList.add((Box) findViewById(R.id.scv10));
        tvList.add((TextView) findViewById(R.id.tv10));
    }

//    ----------------------
//    DATA-SET GETTER
//    ----------------------

    //  restituisce la lista delle Box della Row
    public List<Box> getScvList() {
        return scvList;
    }

    //  restituisce la lista delle TextView della Row
    public List<TextView> getTvList() {
        return tvList;
    }


//    ----------------------
//    VIEW SETTER
//    ----------------------

    // Setta il listner per le Box della Row (saltandone la prima che è quella con le coordinate)
    public void setClickListener(OnClickListener onClickListener, OnLongClickListener onLongClickListener) {
        for (Box box : scvList.subList(1, scvList.size())) {
            box.setOnClickListener(onClickListener);
            box.setOnLongClickListener(onLongClickListener);
            box.setClickable(true);
        }
    }

    // Setta il la clickabilità delle Box della Row (saltandone la prima)
    @Override
    public void setClickable(boolean clickable) {
        for (Box box : scvList.subList(1, scvList.size())) {
            box.setClickable(clickable);
        }
    }

    //  Setta la Row come prima riga di una Board
    public void setFirstRow() {
        for (Box box : scvList) {
            box.setStatusBorder();
        }
        getTvList().get(0).setText("\\");
        getTvList().get(0).setVisibility(VISIBLE);
        getTvList().get(1).setText("A");
        getTvList().get(1).setVisibility(VISIBLE);
        getTvList().get(2).setText("B");
        getTvList().get(2).setVisibility(VISIBLE);
        getTvList().get(3).setText("C");
        getTvList().get(3).setVisibility(VISIBLE);
        getTvList().get(4).setText("D");
        getTvList().get(4).setVisibility(VISIBLE);
        getTvList().get(5).setText("E");
        getTvList().get(5).setVisibility(VISIBLE);
        getTvList().get(6).setText("F");
        getTvList().get(6).setVisibility(VISIBLE);
        getTvList().get(7).setText("G");
        getTvList().get(7).setVisibility(VISIBLE);
        getTvList().get(8).setText("H");
        getTvList().get(8).setVisibility(VISIBLE);
        getTvList().get(9).setText("I");
        getTvList().get(9).setVisibility(VISIBLE);
        getTvList().get(10).setText("J");
        getTvList().get(10).setVisibility(VISIBLE);
    }

    //  Setta la Row come i-riga di una Board
    public void setMiddleRow(int i) {
        scvList.get(0).setStatusBorder();
        tvList.get(0).setText(String.valueOf(i));
        for (TextView textView : getTvList().subList(1, getTvList().size())) {
            textView.setText("");
            textView.setVisibility(INVISIBLE);
        }
    }

    //  Setta la dimensione del font delle TextView della Row
    public void setFontSize(int i) {
        for (TextView textView : tvList) {
            textView.setTextSize(i);
        }
    }

    //  Setta la i-esima Box della Row come Mancata
    public void setMiss(int i) {
        scvList.get(i).setStatusMissed();
        getTvList().get(i).setText("O");
        getTvList().get(i).setVisibility(VISIBLE);
    }

    //  Setta la i-esima Box della Row come Colpita
    public void setHit(int i) {
        scvList.get(i).setStatusHit();
        tvList.get(i).setText("X");
        tvList.get(i).setVisibility(VISIBLE);
    }

    //  Setta la i-esima Box della Row come Affondata
    public void setSank(int i, BoxOrientation orientation) {
        scvList.get(i).setStatusSank(orientation);
//        setHit(i);
        tvList.get(i).setText("A");
        tvList.get(i).setVisibility(VISIBLE);
    }

    //  Setta la i-esima Box della Row come Visibile(come nave)
    public void setVisibile(int i, BoxOrientation orientation) {
        scvList.get(i).setStatusVisible(orientation);
    }
}
