package com.glab.usisahauchange.viewmodels;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.glab.usisahauchange.database.BalanceRepository;
import com.glab.usisahauchange.models.Change;

import java.util.List;

public class BalanceViewModel extends AndroidViewModel {
    BalanceRepository balanceRepository;
    private LiveData<List<Change>> balances;

    public BalanceViewModel(@NonNull Application application) {
        super(application);

        balanceRepository = new BalanceRepository(application);
        balances = balanceRepository.getBalances();
    }

    public void addBalance (Change change) {
        balanceRepository.addBalance(change);
    }

    public void updateBalance (Change change) {
        balanceRepository.updateBalance(change);
    }

    public void deleteBalace(Change change) {
        balanceRepository.deleteBalance(change);
    }

    public void clearAllBalances(){
        balanceRepository.clearBalances();
    }

    public  LiveData<List<Change>> getAllBalances(){
        return balances;
    }

}
