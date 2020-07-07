package it.tranigrillo.battleship.model.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;


// classe che ha la responsabilità di creare il DB se non è già istanziato
// è una classe che sfrutta il paradigma singleton
// può essere acceduta solo in maniera sincronizzata (da buon DB ;))
// mantiene due entity achievement e clash rispettivamente obbiettivi ottenuti in gioco e cronologia dei risultati delle partite

@Database(entities = {Achievement.class, Clash.class}, version = 1)
public abstract class DataBase extends RoomDatabase {

    private static DataBase instance;

    public abstract AchievementDao achievementDao();
    public abstract ClashDao clashDao();


//--------------------------------------------
//    COSTRUTTORE
//---------------------------------------------

    // controlla se già esiste un DB, in caso affermativo ne restituisce un'istanza
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

    // callbak per inserire nuovi dati nella prima istanziazione del DB
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    // classe che implementa il primo popolamento del DB
    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void> {
        private AchievementDao achievementDao;
        private ClashDao clashDao;

        public PopulateDbAsyncTask(DataBase db) {
            achievementDao = db.achievementDao();
            clashDao = db.clashDao();
        }


        // i valori che vengono inseriti
        @Override
        protected Void doInBackground(Void... voids) {
            achievementDao.insert(new Achievement("Genius GoldFish", "you know how start a game", true));
            achievementDao.insert(new Achievement("A child play", "you win a very easy game", true));
            Clash clash = new Clash();
            clash.setTitle("No previous clash");
            clashDao.insert(clash);
            return null;
        }
    }
}
