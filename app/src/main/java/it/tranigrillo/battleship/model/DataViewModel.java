package it.tranigrillo.battleship.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.tranigrillo.battleship.model.database.Achievement;
import it.tranigrillo.battleship.model.database.AchievementDatabaseAPI;
import it.tranigrillo.battleship.model.database.Clash;
import it.tranigrillo.battleship.model.database.ClashDatabaseAPI;

public class DataViewModel extends AndroidViewModel {
    private AchievementDatabaseAPI achievementDatabaseAPI;
    private ClashDatabaseAPI clashDatabaseAPI;
    private LiveData<List<Achievement>> allAchievements;
    private LiveData<List<Clash>> allClashes;

    public DataViewModel(@NonNull Application application) {
        super(application);
        achievementDatabaseAPI = new AchievementDatabaseAPI(application);
        clashDatabaseAPI = new ClashDatabaseAPI(application);
        allAchievements = achievementDatabaseAPI.getAllAchievement();
        allClashes = clashDatabaseAPI.getAllClash();
    }

    public void insert(Achievement achievement) {
        achievementDatabaseAPI.insert(achievement);
    }

    public void insert(Clash clash) {
        clashDatabaseAPI.insert(clash);
    }

    public void update(Achievement achievement) {
        achievementDatabaseAPI.update(achievement);
    }

    public void update(Clash clash) {
        clashDatabaseAPI.update(clash);
    }

    public void delete(Achievement achievement) {
        achievementDatabaseAPI.delete(achievement);
    }

    public void delete(Clash clash) {
        clashDatabaseAPI.delete(clash);
    }

    public void deleteAllAchievements() {
        achievementDatabaseAPI.deleteAll();
    }

    public void deleteAllClashes() {
        clashDatabaseAPI.deleteAll();
    }

    public LiveData<List<Achievement>> getAllAchievements() {
        return allAchievements;
    }

    public LiveData<List<Clash>> getAllClashes() {
        return allClashes;
    }
}
