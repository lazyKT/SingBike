package com.example.singbike.Networking.Requests;

public class RideCreateRequest {

    private int cust_id;
    private String path, start_point, end_point;

    public RideCreateRequest (int customerId, String path, String startPoint, String endPoint) {
        this.cust_id = customerId;
        this.path = path;
        this.end_point = endPoint;
        this.start_point = startPoint;
    }

    public void setCustomerId (int customerId) {
        this.cust_id = customerId;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public void setStartPoint (String startPoint) {
        this.start_point = startPoint;
    }

    public void setEndPoint (String endPoint) {
        this.end_point = endPoint;
    }

}
