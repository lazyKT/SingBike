package com.example.singbike.Networking.Requests;


public class TripRequest {

    public static class TripCreateRequest {

        private int cust_id;
        private String start_point, end_point, path;
        private double fare, distance, promo, total;

        public TripCreateRequest () {}


        public TripCreateRequest (int cust_id, String start_point) {
            this.cust_id = cust_id;
            this.start_point = start_point;
            this.end_point = "";
            this.fare = 0.00;
            this.promo = 0.00;
            this.total = 0.00;
            this.distance = 0.00;
        }
    }

    public static class TripEndRequest {

        private String end_point, path;
        private double fare, distance, promo, total_fare;

        public TripEndRequest (String end_point, String path, double fare, double distance, double promo, double total_fare) {
            this.distance = distance;
            this.end_point = end_point;
            this.fare = fare;
            this.promo = promo;
            this.path = path;
            this.total_fare = total_fare;
        }

    }

}
