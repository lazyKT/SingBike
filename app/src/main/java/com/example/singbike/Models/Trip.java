package com.example.singbike.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Trip implements Parcelable {

    private final int customer_id, trip_id;
    private String start_point, end_point, path; private final String created_at;
    private double fare, promo, distance, time_taken;

    public Trip (JSONObject object) throws JSONException {
        this.customer_id = object.getInt ("customer_id");
        this.trip_id = object.getInt ("trip_id");
        this.start_point = object.getString ("start_point");
        this.end_point = object.getString ("end_point");
        this.path = object.getString ("path");
        this.created_at = object.getString ("created_at");
        this.fare = object.getDouble ("fare");
        this.distance = object.getDouble ("distance");
        this.promo = object.getDouble ("promo");
        this.time_taken = object.getDouble ("time_taken");
    }

    protected Trip(Parcel in) {
        customer_id = in.readInt();
        trip_id = in.readInt();
        start_point = in.readString();
        end_point = in.readString();
        path = in.readString();
        created_at = in.readString();
        fare = in.readDouble();
        promo = in.readDouble();
        distance = in.readDouble();
        time_taken = in.readDouble();
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(customer_id);
        dest.writeInt(trip_id);
        dest.writeString(start_point);
        dest.writeString(end_point);
        dest.writeString(path);
        dest.writeString(created_at);
        dest.writeDouble(fare);
        dest.writeDouble(promo);
        dest.writeDouble(distance);
        dest.writeDouble(time_taken);
    }

    public void setStarting_point (String starting_point) {
        this.start_point = starting_point;
    }

    public void setEnd_point (String end_point) {
        this.end_point = end_point;
    }

    public void setDistance (double distance) {
        this.distance = distance;
    }

    public void setFare (double fare) {
        this.fare = fare;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public void setPromo (double promo) {
        this.promo = promo;
    }

    public void setTime_taken (double time_taken) {
        this.time_taken = time_taken;
    }

    public int getTripID () {
        return this.trip_id;
    }
}
