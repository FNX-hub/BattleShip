package it.tranigrillo.battleship.model.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Achievement achievement);

    @Update
    void update(Achievement achievement);

    @Delete
    void delete(Achievement achievement);

    @Query("DELETE FROM achievement_table")
    void deleteAllAchievement();

    @Query("SELECT * FROM achievement_table ORDER BY locked")
    LiveData<List<Achievement>> getAllAchievements();
}
