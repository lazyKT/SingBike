package com.example.singbike.NetworkRequests;

import java.util.ArrayList;
import java.util.Locale;

public class RideUpdateRequest {

    private int customerID, tripID;
    private double distance, fare, promo, total;
    private String endPoint, path;

    public RideUpdateRequest (int customerID, int tripID, double distance, double fare, double promo,
                              double total, String endPoint) {
        this.customerID = customerID;
        this.tripID = tripID;
        this.distance = distance;
        this.fare = fare;
        this.promo = promo;
        this.total = total;
        this.endPoint = endPoint;
        this.path = "";
    }

    public void setCustomerID (int customerID) {
        this.customerID = customerID;
    }

    public void setTripID (int tripID) {
        this.tripID = tripID;
    }

    public void setDistance (double distance) {
        this.distance = distance;
    }

    public void setFare (double fare) {
        this.fare = fare;
    }

    public void setPromo (double promo) {
        this.promo = promo;
    }

    public void setTotal (double total) {
        this.total = total;
    }

    public void setEndPoint (String endPoint) {
        this.endPoint = endPoint;
    }

    public void setPath (ArrayList<String> points) {

        for (String pts : points) {
            if (this.path.equals(""))
                this.path = String.format (Locale.getDefault(), "%s,%s", this.path, pts);
            else
                this.path = String.format (Locale.getDefault(), ",%s,%s", this.path, pts);
        }
    }
}
