package com.example.singbike.NetworkRequests;

public class RideCreateRequest {

    private int customerId;
    private String path, startPoint, endPoint;

    public RideCreateRequest (int customerId, String path, String startPoint, String endPoint) {
        this.customerId = customerId;
        this.path = path;
        this.endPoint = endPoint;
        this.startPoint = startPoint;
    }

    public void setCustomerId (int customerId) {
        this.customerId = customerId;
    }

    public void setPath (String path) {
        this.path = path;
    }

    public void setStartPoint (String startPoint) {
        this.startPoint = startPoint;
    }

    public void setEndPoint (String endPoint) {
        this.endPoint = endPoint;
    }

}
