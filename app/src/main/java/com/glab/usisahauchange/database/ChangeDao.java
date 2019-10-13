package com.glab.usisahauchange.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.glab.usisahauchange.models.Change;

import java.util.List;

@Dao
public interface ChangeDao {
    @Query("SELECT * FROM change")
    LiveData<List<Change>> getAllBalances();

    @Delete
    void removebalance(Change historyModel);

    @Insert
    void addbalance(Change model);

    @Query("UPDATE change SET alarm_time=:time, change_balance=:balance WHERE id =:id")
    void updateBalance(int balance, int time, int id);

    @Query("DELETE FROM change")
    void clearbalances();
}
