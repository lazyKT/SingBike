/*
 * user model
 */
package com.example.singbike.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String username, email;

    public User () {}

    public User (String username, String email) {
        this.username = username;
        this.email = email;
    }

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
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

    public void setUsername (String username) {
        this.username = username;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public String getUsername () { return this.username; }

    public String getEmail () { return this.email; }

}
