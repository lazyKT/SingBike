package com.example.singbike.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Booking implements Parcelable {

    private String bookingStatus, bookingDate, bookingTime;

    public Booking () {}

    public Booking (String bookingStatus, String bookingDate, String bookingTime) {
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.bookingStatus = bookingStatus;
    }

    protected Booking (Parcel in) {
        this.bookingStatus = in.readString();
        this.bookingTime = in.readString();
        this.bookingDate = in.readString();
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {

        @Override
        public Booking createFromParcel (Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray (int size) {
            return new Booking[size];
        }
    };

    @Override
    public int describeContents () { return 0; }

    @Override
    public void writeToParcel (Parcel in, int flags) {
        in.writeString(this.bookingStatus);
        in.writeString(this.bookingTime);
        in.writeString(this.bookingDate);
    }

    public String getBookingStatus () { return this.bookingStatus; }

    public String getBookingTime () { return this.bookingTime; }

    public String getBookingDate () { return this.bookingDate; }

}
