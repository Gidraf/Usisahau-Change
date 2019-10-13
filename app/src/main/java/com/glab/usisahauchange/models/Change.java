package com.glab.usisahauchange.models;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Change {

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getReminderTime() {
        return reminderTime;
    }


    public void setReminderTime(int reminderTime) {
        this.reminderTime = reminderTime;
    }

    @PrimaryKey(autoGenerate=true)
    private int  id;

    @ColumnInfo(name = "change_balance")
    private int balance;

    @ColumnInfo(name = "alarm_time")
    private int reminderTime;

    public String getCreatedArt() {
        return createdArt;
    }

    public void setCreatedArt(String createdArt) {
        this.createdArt = createdArt;
    }

    @ColumnInfo(name = "createdArt")
    private String createdArt;

    @ColumnInfo(name = "created_date")
    private String createdDate;

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
