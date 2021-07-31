package com.example.singbike.Networking.Requests;

import java.util.ArrayList;
import java.util.Locale;

public class RideUpdateRequest {

    private int cust_id;
    private double distance, fare, promo, total;
    private String end_point, path;

    public RideUpdateRequest (int customerID, double distance, double fare, double promo,
                              double total, String endPoint) {
        this.cust_id = customerID;
        this.distance = distance;
        this.fare = fare;
        this.promo = promo;
        this.total = total;
        this.end_point = endPoint;
        this.path = "";
    }

    public void setCustomerID (int customerID) {
        this.cust_id = customerID;
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
        this.end_point = endPoint;
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
