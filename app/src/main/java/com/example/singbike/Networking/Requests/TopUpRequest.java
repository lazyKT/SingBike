package com.example.singbike.Networking.Requests;

public class TopUpRequest {

    private final double balance;

    public TopUpRequest (Double balance) {
        this.balance = balance;
    }

    public double getAmount() {
        return balance;
    }
}
