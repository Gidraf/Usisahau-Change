package com.glab.usisahauchange.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.glab.usisahauchange.models.Change;
import com.google.android.material.internal.BaselineLayout;

@Database(entities = Change.class, version = 1)
public abstract class BalanceDb extends RoomDatabase {
    public abstract ChangeDao changeDao();
    private static volatile BalanceDb INSTANCE;

    static BalanceDb getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BalanceDb.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BalanceDb.class, "balances")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
