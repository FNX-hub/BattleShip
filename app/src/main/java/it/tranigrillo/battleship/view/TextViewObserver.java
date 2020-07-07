package it.tranigrillo.battleship.view;

import android.widget.TextView;

import java.util.List;

public class TextViewObserver {
    List<TextView> list;

    public TextViewObserver(List<TextView> list) {
        this.list = list;
    }

    public List<TextView> getList() {
        return list;
    }

    public void setList(List<TextView> list) {
        this.list = list;
    }

    public void updateText(int i, String string, int x) {
        list.get(i).setText(String.format(string, x));
    }
}
