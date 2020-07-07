package it.tranigrillo.battleship.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import it.tranigrillo.battleship.R;

public class Game implements Parcelable {
    private static final String TAG = "Game";

    private String rule;
    private Integer lv;
    private Integer smallShip;
    private Integer mediumShip;
    private Integer largeShip;
    private Integer extraLargeShip;

    public Game(Context context, String rule, int lv) {
        this.rule = rule;
        this.lv = lv;
        if (rule.equals(context.getResources().getString(R.string.standard))) {
            smallShip = 3;
            mediumShip = 2;
            largeShip = 1;
            extraLargeShip = 1;
        }
        if (rule.equals(context.getResources().getString(R.string.only_small))) {
            smallShip = 20;
            mediumShip = 0;
            largeShip = 0;
            extraLargeShip = 0;
        }
        if (rule.equals(context.getResources().getString(R.string.only_extra_large))) {
            smallShip = 0;
            mediumShip = 0;
            largeShip = 0;
            extraLargeShip = 5;
        }
        print();
    }

    public String getRule() {
        return rule;
    }

    public Integer getLv() {
        return lv;
    }

    public Integer getSmallShip() {
        return smallShip;
    }

    public Integer getMediumShip() {
        return mediumShip;
    }

    public Integer getLargeShip() {
        return largeShip;
    }

    public Integer getExtraLargeShip() {
        return extraLargeShip;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rule);
        dest.writeValue(this.lv);
        dest.writeValue(this.smallShip);
        dest.writeValue(this.mediumShip);
        dest.writeValue(this.largeShip);
        dest.writeValue(this.extraLargeShip);
    }

    protected Game(Parcel in) {
        this.rule = in.readString();
        this.lv = (Integer) in.readValue(Integer.class.getClassLoader());
        this.smallShip = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mediumShip = (Integer) in.readValue(Integer.class.getClassLoader());
        this.largeShip = (Integer) in.readValue(Integer.class.getClassLoader());
        this.extraLargeShip = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel source) {
            return new Game(source);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    //debug funcion
    public void print() {
        Log.d(TAG, String.format("RULE: %s %d-%d-%d-%d MODE: %d", rule, smallShip, mediumShip, largeShip, extraLargeShip, lv));
    }
}
