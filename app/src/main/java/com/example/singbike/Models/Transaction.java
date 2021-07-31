package com.example.singbike.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Locale;

public class Transaction implements Parcelable {

    private String type, created_at;
    private double amount;
    private int customer_id, transaction_id;

    public Transaction () {
    }

    public Transaction (int transaction_id, int customer_id, String type, String time, double amount) {
        this.customer_id = customer_id;
        this.transaction_id = transaction_id;
        this.created_at = time;
        this.type = type;
        this.amount = amount;
    }

    protected Transaction(Parcel in) {
        type = in.readString();
        created_at = in.readString();
        amount = in.readDouble();
        customer_id = in.readInt();
        transaction_id = in.readInt();
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
        this.created_at = time;
    }

    public void setAmount (Double amount) {
        this.amount = amount;
    }

    public int getTransaction_id () { return  this.transaction_id; }

    public String getTime() {
        return created_at;
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

    @NonNull
    public String toString() {
        return String.format (Locale.getDefault(), "Transaction ID: %d, Type: %s", this.getTransaction_id(), this.getType());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(created_at);
        dest.writeDouble(amount);
    }
}
