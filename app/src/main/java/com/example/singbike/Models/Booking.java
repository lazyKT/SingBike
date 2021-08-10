package com.example.singbike.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.singbike.Utilities.Utils;

public class Booking implements Parcelable {

    private String status, created_at, bike_id;
    private int reservation_id, cust_id;

    public Booking () {}

    public Booking (String bookingStatus, String bookingDate, String bike_id) {
        this.created_at = Utils.toLocalDateTime(bookingDate);
        this.bike_id = bike_id;
        this.status = bookingStatus;
    }

    public Booking (String bookingStatus, String bookingDate, String bike_id, int reservation_id, int cust_id) {
        this.created_at = Utils.toLocalDateTime(bookingDate);
        this.bike_id = bike_id;
        this.status = bookingStatus;
        this.reservation_id = reservation_id;
        this.cust_id = cust_id;
    }

    protected Booking (Parcel in) {
        this.status = in.readString();
        this.bike_id = in.readString();
        this.created_at = in.readString();
        this.cust_id = in.readInt();
        this.reservation_id = in.readInt();
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
        in.writeString(this.status);
        in.writeString(this.bike_id);
        in.writeString(this.created_at);
        in.writeInt (this.cust_id);
        in.writeInt (this.reservation_id);
    }

    public String getBookingStatus () { return this.status; }

    public String getBookingTime () { return this.bike_id; }

    public String getBookingDate () { return this.created_at; }

    public String getBookingDateTime () {
        return String.format("%s %s", this.getBookingDate(), getBookingTime());
    }

    public int getReservation_id () { return  this.reservation_id; }

    public String getBike_id () { return this.bike_id; }

}
