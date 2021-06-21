package com.example.singbike.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Ride implements Parcelable {

    private String rideTime, rideDistance, rideFare, promo;

    public Ride () { /* default constructor */ }

    public Ride (String rideTime, String rideDistance, String rideFare, String promo) {
        this.rideDistance = rideDistance;
        this.rideTime = rideTime;
        this.rideFare = rideFare;
        this.promo = promo;
    }

    public Ride (Parcel parcel) {
        this.rideDistance = parcel.readString();
        this.rideTime = parcel.readString();
        this.promo = parcel.readString();
        this.rideFare = parcel.readString();
    }

    public static final Creator<Ride> CREATOR = new Creator<Ride>() {
        @Override
        public Ride createFromParcel(Parcel source) {
            return new Ride(source);
        }

        @Override
        public Ride[] newArray(int size) {
            return new Ride[size];
        }
    };

    public void setRideTime (String rideTime) {
        this.rideTime = rideTime;
    }

    public void setRideDistance (String rideDistance) {
        this.rideDistance = rideDistance;
    }

    public String getRideTime () { return this.rideTime; }

    public String getRideDistance () { return this.rideDistance; }

    public String getRideFare () { return this.rideFare; }

    public String getPromo () { return this.promo; }

    public String getTotalFare () {
         double totalFare = Double.parseDouble(this.rideFare) - Double.parseDouble(this.promo);
         return String.valueOf (totalFare);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeString (this.rideDistance);
        dest.writeString (this.rideTime);
        dest.writeString (this.rideFare);
        dest.writeString (this.promo);
    }
}
