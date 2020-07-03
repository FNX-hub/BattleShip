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
public interface ClashDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Clash clash);

    @Update
    void update(Clash clash);

    @Delete
    void delete(Clash clash);

    @Query("DELETE FROM clash_table")
    void deleteAllClash();

    @Query("SELECT * FROM clash_table ORDER BY date")
    LiveData<List<Clash>> getAllClash();
}
