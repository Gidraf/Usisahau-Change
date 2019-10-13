package com.glab.usisahauchange.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.glab.usisahauchange.models.Change;

import java.util.List;

public class BalanceRepository {
    private  BalanceDb balanceDb;
    public LiveData<Change> balance;
    public LiveData<List<Change>> balances;
    ChangeDao changeDao;
    Context context;
    ChangeDao dao;

    public BalanceRepository(Context context) {
        this.context = context;
        balanceDb = BalanceDb.getDatabase(context);
        changeDao = balanceDb.changeDao();
        balances  = changeDao.getAllBalances();
    }

    public LiveData<List<Change>> getBalances(){
        return balances;
    }

    public void addBalance (Change model) {
        new insertBalance(changeDao).execute(model);
    }

    public void updateBalance (Change model) {
        new updateBalance(changeDao).execute(model);
    }

    public  void clearBalances(){
        new clearAllbalancesAsyncTask(changeDao).execute();
    }

    public void deleteBalance(Change model){
        new deleteBalanceAsyncTask(changeDao).execute(model);
    }

    private static class insertBalance extends AsyncTask<Change, Void, Void> {

        private ChangeDao mAsyncTaskDao;

        insertBalance(ChangeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Change... params) {
            mAsyncTaskDao.addbalance(params[0]);
            return null;
        }
    }


    private static class updateBalance extends AsyncTask<Change, Void, Void> {

        private ChangeDao mAsyncTaskDao;

        updateBalance(ChangeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Change... params) {
            mAsyncTaskDao.updateBalance(params[0].getBalance(), params[0].getReminderTime(), params[0].getId());
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            return null;
        }
    }

    private static class deleteBalanceAsyncTask extends AsyncTask<Change, Void, Void> {

        private ChangeDao mAsyncTaskDao;

        deleteBalanceAsyncTask(ChangeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Change... params) {
            mAsyncTaskDao.removebalance(params[0]);
            return null;
        }
    }

    private static class clearAllbalancesAsyncTask extends AsyncTask<Void, Void, Void> {

        private ChangeDao mAsyncTaskDao;

        clearAllbalancesAsyncTask(ChangeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mAsyncTaskDao.clearbalances();
            return null;
        }
    }
}
