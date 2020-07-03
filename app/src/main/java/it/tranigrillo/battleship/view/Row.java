package it.tranigrillo.battleship.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import it.tranigrillo.battleship.R;

public class Row extends LinearLayout {
    private List<SquareCardView> scvList = new ArrayList<>();
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

        scvList.add((SquareCardView) findViewById(R.id.scv0));
        tvList.add((TextView) findViewById(R.id.tv0));

        scvList.add((SquareCardView) findViewById(R.id.scv1));
        tvList.add((TextView) findViewById(R.id.tv1));

        scvList.add((SquareCardView) findViewById(R.id.scv2));
        tvList.add((TextView) findViewById(R.id.tv2));

        scvList.add((SquareCardView) findViewById(R.id.scv3));
        tvList.add((TextView) findViewById(R.id.tv3));

        scvList.add((SquareCardView) findViewById(R.id.scv4));
        tvList.add((TextView) findViewById(R.id.tv4));

        scvList.add((SquareCardView) findViewById(R.id.scv5));
        tvList.add((TextView) findViewById(R.id.tv5));

        scvList.add((SquareCardView) findViewById(R.id.scv6));
        tvList.add((TextView) findViewById(R.id.tv6));

        scvList.add((SquareCardView) findViewById(R.id.scv7));
        tvList.add((TextView) findViewById(R.id.tv7));

        scvList.add((SquareCardView) findViewById(R.id.scv8));
        tvList.add((TextView) findViewById(R.id.tv8));

        scvList.add((SquareCardView) findViewById(R.id.scv9));
        tvList.add((TextView) findViewById(R.id.tv9));

        scvList.add((SquareCardView) findViewById(R.id.scv10));
        tvList.add((TextView) findViewById(R.id.tv10));
    }

    public List<SquareCardView> getScvList() {
        return scvList;
    }

    public List<TextView> getTvList() {
        return tvList;
    }

    public void setFirstRow() {
        for (SquareCardView box : scvList) {
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

    public void setMiddleRow(int i) {
        scvList.get(0).setStatusBorder();
        tvList.get(0).setText(String.valueOf(i));
        for (TextView textView : getTvList().subList(1, getTvList().size())) {
            textView.setText("");
            textView.setVisibility(INVISIBLE);
        }
    }

    public void setHit(int i) {
        scvList.get(i).setStatusHit();
        tvList.get(i).setText("X");
        tvList.get(i).setVisibility(VISIBLE);
    }

    public void setMiss(int i) {
        scvList.get(i).setStatusMissed();
        getTvList().get(i).setText("O");
        getTvList().get(i).setVisibility(VISIBLE);
    }
}
