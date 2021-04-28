package com.example.singbike.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

public class Transaction implements Parcelable {

    private String type, time;
    private double amount;

    public Transaction () {
    }

    public Transaction (String type, String time, double amount) {
        this.time = time;
        this.type = type;
        this.amount = amount;
    }

    protected Transaction(Parcel in) {
        type = in.readString();
        time = in.readString();
        amount = in.readDouble();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public void setType (String type) {
        this.type = type;
    }

    public void setTime (String time) {
        this.time = time;
    }

    public void setAmount (Double amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getAmountStr () {
        /* get Amount String based on Transaction Type */
        char symbol = this.getType().equals("Top Up") ? '+' : '-';

        return String.format (Locale.ENGLISH, "%c%.2f", symbol, this.getAmount());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(time);
        dest.writeDouble(amount);
    }
}
