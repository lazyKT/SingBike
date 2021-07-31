package com.example.singbike.Networking.Requests;

public class TransactionRequest {

    private int cust_id;
    private double amount;
    private String type;

    public TransactionRequest (int cust_id, double amount, String type) {
        this.cust_id = cust_id;
        this.amount = amount;
        this.type = type;
    }

    public void setCust_id (int cust_id) {
        this.cust_id = cust_id;
    }

    public void setAmount (double amount) {
        this.amount = amount;
    }

    public void setType (String type) {
        this.type = type;
    }

}
