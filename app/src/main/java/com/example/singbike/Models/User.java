/*
 * user model
 */
package com.example.singbike.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class User implements Parcelable {

    private String username, email, password, created_at, updated_at;
    private double balance;
    private int id, credits;

    public User () {}

    public User (User user1) {
        this.username = user1.username;
        this.email = user1.email;
        this.password = user1.password;
        this.credits = user1.credits;
        this.created_at = user1.created_at;
        this.updated_at = user1.updated_at;
        this.id = user1.id;
        this.balance = user1.balance;
    }

    public User (JSONObject object) throws JSONException {
        this.id = object.getInt ("id");
        this.username = object.getString ("username");
        this.email = object.getString ("email");
        String balanceStr = String.format (Locale.getDefault(), "%.2f", object.getDouble ("balance"));
        this.balance = Double.parseDouble (balanceStr);
        this.credits = object.getInt ("credits");
        this.updated_at = object.getString ("updated_at");
        this.created_at = object.getString ("created_at");
    }

    public User (String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User (String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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

    public void setPassword (String password) { this.password = password; }

    public void setCreated_at (String created_at) { this.created_at = created_at; }

    public void setUpdated_at (String updated_at) { this.updated_at = updated_at; }

    public void setBalance (Double balance) {
        String balanceStr = String.format (Locale.getDefault(), "%.2f", balance);
        this.balance = Double.parseDouble (balanceStr);
    }

    public void setCredits (int credits) { this.credits = credits; }

    public int getID () { return this.id; }

    public String getUsername () { return this.username; }

    public String getEmail () { return this.email; }

    public String getPassword () { return this.password; }

    public String getCreated_at () { return this.created_at; }

    public String getUpdated_at () { return this.updated_at; }

    public Double getBalance () { return this.balance; }

    public int getCredits () { return this.credits; }

    @NonNull
    public String toString () {
        return String.format (Locale.getDefault(), "id: %d, username: %s, email: %s, credits: %d, balance: %.2f\n",
                this.getID(), this.getUsername(), this.getEmail(), this.getCredits(), this.getBalance());
    }

}
