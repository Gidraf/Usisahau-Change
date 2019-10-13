package com.glab.usisahauchange;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.glab.usisahauchange.adapters.MainBalanceAdapter;
import com.glab.usisahauchange.fragments.AddBalance;
import com.glab.usisahauchange.models.Change;
import com.glab.usisahauchange.services.AlertReceiver;
import com.glab.usisahauchange.services.AlertUserService;
import com.glab.usisahauchange.utils.EditChangeListener;
import com.glab.usisahauchange.utils.WakeLocker;
import com.glab.usisahauchange.viewmodels.BalanceViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener, EditChangeListener {
    BalanceViewModel balanceViewModel;
    private Intent playIntent;

    RecyclerView.LayoutManager balanceManager;
    MainBalanceAdapter balanceAdapter;
    RecyclerView balanceRecyclerView;
    FloatingActionButton addBalanceButton;
    private boolean alertBound=false;
    AlertUserService alertService;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    Button cancelBtn, saveBtn;
    EditText balanceInput, timeRemindInput;
    boolean isEdit = false;
    int changId;
    boolean haclickedBefore = false;
    SharedPreferences.Editor e;
    //  Initialize SharedPreferences
    SharedPreferences getPrefs;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        getPrefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());
        e = getPrefs.edit();
        balanceViewModel = ViewModelProviders.of(this).get(BalanceViewModel.class);
        balanceManager = new LinearLayoutManager(this);
        balanceAdapter = new MainBalanceAdapter(this, balanceViewModel, this);
        balanceRecyclerView = binding.getRoot().findViewById(R.id.balance_list);
        addBalanceButton = binding.getRoot().findViewById(R.id.add_balance_button);
        mAdView =  binding.getRoot().findViewById(R.id.home_banner);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        addBalanceButton.setOnClickListener(this);
        balanceRecyclerView.setHasFixedSize(true);
        balanceRecyclerView.setLayoutManager(balanceManager);
        balanceViewModel.getAllBalances().observe(this, new Observer<List<Change>>() {
            @Override
            public void onChanged(List<Change> changes) {
                balanceAdapter.setBalances(changes);
            }
        });
        balanceRecyclerView.setAdapter(balanceAdapter);
        init();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                if (isFirstStart) {

                    final Intent i = new Intent(Home.this, MainActivity.class);

                    runOnUiThread(new Runnable() {
                        @Override public void run() {
                            startActivity(i);
                        }
                    });

                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();
    }


    private void init() {
        builder = new AlertDialog.Builder(Home.this);
        View addView = LayoutInflater.from(Home.this).inflate(R.layout.fragment_add_balance, null);
        builder.setView(addView);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        cancelBtn = addView.findViewById(R.id.cancel_btn);
        saveBtn = addView.findViewById(R.id.save_button);
        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        saveBtn.setAlpha(0.7f);
        saveBtn.setEnabled(false);
        balanceInput = addView.findViewById(R.id.balance_input);
        timeRemindInput = addView.findViewById(R.id.remind_time_input);
        cancelBtn = addView.findViewById(R.id.cancel_btn);
        saveBtn = addView.findViewById(R.id.save_button);
        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        saveBtn.setAlpha(0.7f);
        saveBtn.setEnabled(false);
        balanceInput = addView.findViewById(R.id.balance_input);
        timeRemindInput = addView.findViewById(R.id.remind_time_input);

        balanceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length()==0 ){
                    saveBtn.setAlpha(0.7f);
                    saveBtn.setEnabled(false);
                    return;
                }
                saveBtn.setAlpha(1);
                saveBtn.setEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length()==0 || balanceInput.getText().toString().isEmpty()){
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }
                    if ( (!balanceInput.getText().toString().isEmpty() && !timeRemindInput.getText().toString().isEmpty()) && (Integer.parseInt(balanceInput.getText().toString()) == 0 || Integer.parseInt(timeRemindInput.getText().toString()) ==0)){
                        Toast.makeText(Home.this, "Weka at least 1 bob or 1 min", Toast.LENGTH_LONG).show();
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }

                    if (Integer.parseInt(String.valueOf(s)) >1000){
                        Toast.makeText(Home.this, "Pesa kubwa Kenya ni 1000", Toast.LENGTH_LONG).show();
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }

                    if (timeRemindInput.getText().toString().isEmpty()){
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }
                    saveBtn.setAlpha(1);
                    saveBtn.setEnabled(true);
                }
                catch (Exception e){
                    Toast.makeText(Home.this,
                            "Kuna kaerror kametokezea, tafadhali rudia tena",
//                            e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                s.append(" bob");
            }
        });

        timeRemindInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length()==0){
                    saveBtn.setAlpha(0.7f);
                    saveBtn.setEnabled(false);
                    return;
                }
                saveBtn.setAlpha(1);
                saveBtn.setEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() == 0 || timeRemindInput.getText().toString().isEmpty()){
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }
                    if ( (!balanceInput.getText().toString().isEmpty() && !timeRemindInput.getText().toString().isEmpty()) && (Integer.parseInt(timeRemindInput.getText().toString()) ==0 || Integer.parseInt(balanceInput.getText().toString()) ==0)){
                        Toast.makeText(Home.this, "Weka at least 1 min or 1 bob", Toast.LENGTH_LONG).show();
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }

                    if (!balanceInput.getText().toString().isEmpty() && Integer.parseInt(balanceInput.getText().toString()) >1000){
                        Toast.makeText(Home.this, "Pesa kubwa Kenya ni 1000", Toast.LENGTH_LONG).show();
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }
                    if (balanceInput.getText().toString().isEmpty()){
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }
                    saveBtn.setAlpha(1);
                    saveBtn.setEnabled(true);
                }
                catch (Exception e){
                    Toast.makeText(Home.this,
                            "Kuna kaerror kametokezea tafadhali rudia tena",
//                            e.getMessage(),

                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                s.append(" bob");
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v==addBalanceButton){
            AddBalance addBalance = new AddBalance();
//            addBalance.show(getSupportFragmentManager(),"Add balance");
            if (alertService.isOnProgress()){
                Toast.makeText(alertService, "Wait kiasi kuna change haujarudishiwa bado. \uD83D\uDE03", Toast.LENGTH_LONG).show();
                return;
            }
            dialog.show();
        }
        else if (v==saveBtn){
            Date date = Calendar.getInstance().getTime();
            Change change = new Change();
            change.setReminderTime(Integer.parseInt(timeRemindInput.getText().toString()));
            change.setBalance(Integer.parseInt(balanceInput.getText().toString()));
            Intent alertIntent = new Intent(Home.this, AlertReceiver.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(Home.this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (!isEdit){
                change.setCreatedArt(new SimpleDateFormat("hh:mm a").format(date));
                change.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy").format(date));
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + change.getReminderTime()*60000, pendingIntent);
                if (alertService!=null) {
                    alertService.setOnProgress(true);
                    if (alertService.isOnProgress()){
                        Toast.makeText(Home.this, "Tulia kiasi kuna change haujapew", Toast.LENGTH_SHORT).show();
                    }
                }
                balanceViewModel.addBalance(change);
                Toast.makeText(Home.this, "Wazi, we tulia nitakushtua.", Toast.LENGTH_LONG).show();
            }
            else {
                change.setId(changId);
                balanceViewModel.updateBalance(change);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + change.getReminderTime()*60000, pendingIntent);
                if (alertService!=null) {
                    if (alertService.isOnProgress()){
                        alertService.setOnProgress(true);
                    }

                }
                Toast.makeText(Home.this, "Wazi nimeupdate, we relax tu.", Toast.LENGTH_LONG).show();
            }

            dialog.dismiss();
            balanceInput.setText("");
            timeRemindInput.setText("");
            isEdit = false;
        }
        else if (v==cancelBtn){
            dialog.dismiss();
            balanceInput.setText("");
            timeRemindInput.setText("");
            isEdit = false;
        }
    }

    @Override
    public void onEditChange(Change change) {
        changId = change.getId();
        balanceInput.setText(String.valueOf(change.getBalance()));
        timeRemindInput.setText(String.valueOf(change.getReminderTime()));
        isEdit = true;
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (playIntent == null) {
                playIntent = new Intent(this, AlertUserService.class);
                bindService(playIntent, alertConnection, Context.BIND_AUTO_CREATE);
                startService(playIntent);

            }
        }
        catch (Exception e){
            Toast.makeText(Home.this, "Kuna error imetokea but usikatch", Toast.LENGTH_SHORT).show();
        }
    }

    private ServiceConnection alertConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            AlertUserService.AlertBinder binder = (AlertUserService.AlertBinder)service;
            alertService = binder.getService();
            alertBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("alertStatus", alertBound);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        alertBound = savedInstanceState.getBoolean("alertStatus");
    }

    @Override
    public void onBackPressed() {
        if (!haclickedBefore){
            Toast.makeText(Home.this, "Finya tena ujitoe", Toast.LENGTH_SHORT).show();
            haclickedBefore = true;
            return;
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (alertService != null) {
            this.unbindService(alertConnection);
        }
    }
}
