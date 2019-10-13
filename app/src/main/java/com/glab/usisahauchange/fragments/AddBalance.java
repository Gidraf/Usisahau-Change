package com.glab.usisahauchange.fragments;


import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.glab.usisahauchange.R;
import com.glab.usisahauchange.models.Change;
import com.glab.usisahauchange.services.AlertUserService;
import com.glab.usisahauchange.viewmodels.BalanceViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBalance extends DialogFragment implements View.OnClickListener {
    BalanceViewModel balanceViewModel;
    Button cancelBtn, saveBtn;
    EditText balanceInput, timeRemindInput;
    boolean isEdit = false;
    int changeId;
    private boolean alertBound=false;
    AlertUserService alertService;

    public AddBalance() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        balanceViewModel = ViewModelProviders.of(this).get(BalanceViewModel.class);
        View view = inflater.inflate(R.layout.fragment_add_balance, container, false);
        cancelBtn = view.findViewById(R.id.cancel_btn);
        saveBtn = view.findViewById(R.id.save_button);
        cancelBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        saveBtn.setAlpha(0.7f);
        saveBtn.setEnabled(false);
        balanceInput = view.findViewById(R.id.balance_input);
        timeRemindInput = view.findViewById(R.id.remind_time_input);
         if(getArguments() != null){
             String balance = String.valueOf(getArguments().getInt("balance"));
             String time = String.valueOf(getArguments().getInt("time"));
             changeId = getArguments().getInt("id");
             balanceInput.setText(balance);
             timeRemindInput.setText(time);
             isEdit = true;
         }

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
                        Toast.makeText(getContext(), "Weka at least 1 bob", Toast.LENGTH_LONG).show();
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }

                    if (Integer.parseInt(String.valueOf(s)) >1000){
                        Toast.makeText(getContext(), "Pesa kubwa Kenya ni 1000", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getContext(),
                            "Kuna kaerror kametokezea, tafadhali rudia tena",
//                            e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    dismiss();
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
                        Toast.makeText(getContext(), "Weka at least 1 min", Toast.LENGTH_LONG).show();
                        saveBtn.setAlpha(0.7f);
                        saveBtn.setEnabled(false);
                        return;
                    }

                    if (!balanceInput.getText().toString().isEmpty() && Integer.parseInt(balanceInput.getText().toString()) >1000){
                        Toast.makeText(getContext(), "Pesa kubwa Kenya ni 1000", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getContext(),
                            "Kuna kaerror kametokezea tafadhali rudia tena",
//                            e.getMessage(),

                            Toast.LENGTH_SHORT).show();
                    dismiss();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
               s.append(" bob");
            }
        });

        return  view;
    }

    @Override
    public void onClick(View v) {
        if (v==saveBtn){
            Date date = Calendar.getInstance().getTime();
            Change change = new Change();
            change.setReminderTime(Integer.parseInt(timeRemindInput.getText().toString()));
            change.setBalance(Integer.parseInt(balanceInput.getText().toString()));
            if (!isEdit){
                change.setCreatedArt(new SimpleDateFormat("hh:mm a").format(date));
                change.setCreatedDate(new SimpleDateFormat("dd-MM-yyyy").format(date));
                balanceViewModel.addBalance(change);
                Toast.makeText(getContext(), "Wazi, we tulia nitakushtua.", Toast.LENGTH_LONG).show();
            }
            else {
                change.setId(changeId);
                balanceViewModel.updateBalance(change);
                Toast.makeText(getContext(), "Wazi nimeupdate, we relax tu.", Toast.LENGTH_LONG).show();
            }

            dismiss();
        }
        else if (v==cancelBtn){
            dismiss();
        }
    }
}
