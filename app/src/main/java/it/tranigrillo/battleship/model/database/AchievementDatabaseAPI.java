package it.tranigrillo.battleship.model.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;


// classe che ha lo scopo di essere un API per lavorare sul DB in maniera agevole,
// offrendo dei metodi alle operazioni sull'entity Achievement
public class AchievementDatabaseAPI {
    private AchievementDao achievementDao;
    private LiveData<List<Achievement>> allAchievement;

    public AchievementDatabaseAPI(Application application) {
        DataBase dataBase = DataBase.getInstance(application);
        achievementDao = dataBase.achievementDao();
        allAchievement = achievementDao.getAllAchievements();
    }

    public void insert(Achievement achievement) {
        new InsertAchievementAsyncTask(achievementDao).execute(achievement);
    }

    public void update(Achievement achievement) {
        new UpdateAchievementAsyncTask(achievementDao).execute(achievement);
    }

    public void delete(Achievement achievement) {
        new DeleteAchievementAsyncTask(achievementDao).execute(achievement);
    }

    public void deleteAll() {
        new DeleteAllAchievementAsyncTask(achievementDao).execute();
    }

    public LiveData<List<Achievement>> getAllAchievement() {
        return allAchievement;
    }


    // metodi per lavorare in modo asincrono sul DB
    private static class InsertAchievementAsyncTask extends AsyncTask<Achievement, Void, Void> {

        private AchievementDao achievementDao;

        InsertAchievementAsyncTask(AchievementDao achievementDao) {
            this.achievementDao = achievementDao;
        }

        @Override
        protected Void doInBackground(Achievement... achievements) {
            achievementDao.insert(achievements[0]);
            return null;
        }
    }

    private static class UpdateAchievementAsyncTask extends AsyncTask<Achievement, Void, Void> {

        private AchievementDao achievementDao;

        UpdateAchievementAsyncTask(AchievementDao achievementDao) {
            this.achievementDao = achievementDao;
        }

        @Override
        protected Void doInBackground(Achievement... achievements) {
            achievementDao.update(achievements[0]);
            return null;
        }
    }

    private static class DeleteAchievementAsyncTask extends AsyncTask<Achievement, Void, Void> {

        private AchievementDao achievementDao;

        DeleteAchievementAsyncTask(AchievementDao achievementDao) {
            this.achievementDao = achievementDao;
        }

        @Override
        protected Void doInBackground(Achievement... achievements) {
            achievementDao.update(achievements[0]);
            return null;
        }
    }

    private static class DeleteAllAchievementAsyncTask extends AsyncTask<Void, Void, Void> {

        private AchievementDao achievementDao;

        DeleteAllAchievementAsyncTask(AchievementDao achievementDao) {
            this.achievementDao = achievementDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            achievementDao.deleteAllAchievement();
            return null;
        }
    }
}
