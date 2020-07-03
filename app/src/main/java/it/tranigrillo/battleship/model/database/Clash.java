package it.tranigrillo.battleship.model.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clash_table")
public class Clash {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "game_time")
    private String gameTime;

    @ColumnInfo(name = "victory")
    private boolean victory;

    @ColumnInfo(name = "date")
    private String date;

    public Clash(String title, String gameTime, boolean victory, String date) {
        this.title = title;
        this.gameTime = gameTime;
        this.victory = victory;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGameTime() {
        return gameTime;
    }

    public boolean isVictory() {
        return victory;
    }

    public String getDate() {
        return date;
    }
}
