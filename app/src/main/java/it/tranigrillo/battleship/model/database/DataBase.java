package it.tranigrillo.battleship.model.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.sql.Date;
import java.util.Calendar;

@Database(entities = {Achievement.class, Clash.class}, version = 1)
public abstract class DataBase extends RoomDatabase {

    private static DataBase instance;

    public abstract AchievementDao achievementDao();
    public abstract ClashDao clashDao();

    public static synchronized DataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    DataBase.class, "battleship_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {
        private AchievementDao achievementDao;
        private ClashDao clashDao;

        public PopulateDbAsyncTask(DataBase db) {
            achievementDao = db.achievementDao();
            clashDao = db.clashDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            achievementDao.insert(new Achievement("Genius GoldFish", "you know how start a game", true));
            achievementDao.insert(new Achievement("A child play", "you win a very easy game", true));
            clashDao.insert(new Clash("No previous clash", null, false, Calendar.getInstance().getTime().toString()));
            return null;
        }
    }
}
