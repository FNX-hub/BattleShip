package it.tranigrillo.battleship.model.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

// classe che ha lo scopo di essere un API per lavorare sul DB in maniera agevole,
// offrendo dei metodi alle operazioni sull'entity Clash
public class ClashDatabaseAPI {
    private ClashDao clashDao;
    private LiveData<List<Clash>> allClash;

    public ClashDatabaseAPI(Application application) {
        DataBase dataBase = DataBase.getInstance(application);
        clashDao = dataBase.clashDao();
        allClash = clashDao.getAllClash();
    }

    public void insert(Clash clash) {
        new InsertClashAsyncTask(clashDao).execute(clash);
    }

    public void update(Clash clash) {
        new UpdateClashAsyncTask(clashDao).execute(clash);
    }

    public void delete(Clash clash) {
        new DeleteClashAsyncTask(clashDao).execute(clash);
    }

    public void deleteAll() {
        new DeleteAllClashAsyncTask(clashDao).execute();
    }

    public LiveData<List<Clash>> getAllClash() {
        return allClash;
    }

    // metodi per lavorare in modo asincrono sul DB
    private static class InsertClashAsyncTask extends AsyncTask<Clash, Void, Void> {

        private ClashDao clashDao;

        InsertClashAsyncTask(ClashDao clashDao) {
            this.clashDao = clashDao;
        }

        @Override
        protected Void doInBackground(Clash... clashes) {
            clashDao.insert(clashes[0]);
            return null;
        }
    }

    private static class UpdateClashAsyncTask extends AsyncTask<Clash, Void, Void> {

        private ClashDao clashDao;

        UpdateClashAsyncTask(ClashDao clashDao) {
            this.clashDao = clashDao;
        }

        @Override
        protected Void doInBackground(Clash... clashes) {
            clashDao.update(clashes[0]);
            return null;
        }
    }

    private static class DeleteClashAsyncTask extends AsyncTask<Clash, Void, Void> {

        private ClashDao clashDao;

        DeleteClashAsyncTask(ClashDao clashDao) {
            this.clashDao = clashDao;
        }

        @Override
        protected Void doInBackground(Clash... clashes) {
            clashDao.update(clashes[0]);
            return null;
        }
    }

    private static class DeleteAllClashAsyncTask extends AsyncTask<Void, Void, Void> {

        private ClashDao clashDao;

        DeleteAllClashAsyncTask(ClashDao clashDao) {
            this.clashDao = clashDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            clashDao.deleteAllClash();
            return null;
        }
    }
}
