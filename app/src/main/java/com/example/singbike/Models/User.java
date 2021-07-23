/*
 * user model
 */
package com.example.singbike.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Locale;

public class User implements Parcelable {

    private String username, email, created_at, updated_at;
    private double balance;
    private int id, credits;

    public User () {}

    public User (String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User (int id, String username, String email, int credits, double balance, String created_at, String updated_at) {
        this.username = username;
        this.email = email;
        this.created_at = created_at;
        this.credits = credits;
        this.updated_at = updated_at;
        this.balance = balance;
    }

    protected User(Parcel in) {
        id = in.readInt();
        username = in.readString();
        email = in.readString();
        credits = in.readInt();
        balance = in.readDouble();
        created_at = in.readString();
        updated_at = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt (this.id);
        dest.writeString (this.username);
        dest.writeString (this.email);
        dest.writeString (this.created_at);
        dest.writeString (this.updated_at);
        dest.writeInt (this.credits);
        dest.writeDouble (this.balance);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void setID (int id) { this.id = id; }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public void setCreated_at (String created_at) { this.created_at = created_at; }

    public void setUpdated_at (String updated_at) { this.updated_at = updated_at; }

    public void setBalance (Double balance) { this.balance = balance; }

    public void setCredits (int credits) { this.credits = credits; }

    public int getID () { return this.id; }

    public String getUsername () { return this.username; }

    public String getEmail () { return this.email; }

    public String getCreated_at () { return this.created_at; }

    public String getUpdated_at () { return this.updated_at; }

    public Double getBalance () { return this.balance; }

    public int getCredits () { return this.credits; }

    @NonNull
    public String toString () {
        return String.format (Locale.getDefault(), "id: %d, username: %s, email: %s, credits: %d\n",
                this.getID(), this.getUsername(), this.getEmail(), this.getCredits());
    }

}
