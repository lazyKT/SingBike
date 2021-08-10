package com.example.singbike.Networking.Requests;

public class TripRequest {

    private int cust_id, trip_id;
    private String start_point, end_point, path;
    private double fare, distance, promo, total_fare;

    public TripRequest () {}


    public TripRequest (int cust_id, String start_point) {
        this.cust_id = cust_id;
        this.start_point = start_point;
        this.end_point = "";
        this.fare = 0.00;
        this.promo = 0.00;
        this.total_fare = 0.00;
        this.distance = 0.00;
    }

    public TripRequest (int trip_id, String end_point, String path, double promo, double fare, double distance) {
        this.trip_id = trip_id;
        this.end_point = end_point;
        this.path = path;
        this.promo = promo;
        this.fare = fare;
        this.distance = distance;
    }

    public TripRequest (int cust_id, String start_point, String end_point, double fare, double distance, double promo, double total) {
        this.cust_id = cust_id;
        this.start_point = start_point;
        this.end_point = end_point;
        this.promo = promo;
        this.fare = fare;
        this.distance = distance;
        this.total_fare = total;
    }


}
