package it.tranigrillo.battleship.model.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "achievement_table")
public class Achievement {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "locked")
    private boolean locked;

    public Achievement(String title, String description, boolean locked) {
        this.title = title;
        this.description = description;
        this.locked = locked;
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

    public String getDescription() {
        return description;
    }

    public boolean isLocked() {
        return locked;
    }
}
