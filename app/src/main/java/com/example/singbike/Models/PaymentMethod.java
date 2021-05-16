package com.example.singbike.Models;

public class PaymentMethod {

    private String method, type, cardNum, cvc, expiryDate, name;

    public PaymentMethod () {}

    public PaymentMethod (String method, String type,  String cardNum, String expiryDate, String name, String cvc) {
        this.cardNum = cardNum;
        this.type = type;
        this.method = method;
        this.cvc = cvc;
        this.expiryDate = expiryDate;
        this.name = name;
    }

    public String getMethod () { return  this.method; }

    public String getType () { return this.type; }

    public String getCardNum () { return this.cardNum; }

    public String getCvc () { return this.cvc; }

    public String getExpiryDate () { return this.expiryDate; }

    public String getName () { return this.name; }

    public void setMethod (String method) {
        this.method = method;
    }

    public void setType (String type) {
        this.type = type;
    }

    public void setCardNum (String cardNum) {
        this.cardNum = cardNum;
    }

    public void setCvc (String cvc) {
        this.cvc = cvc;
    }

    public void setExpiryDate (String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setName (String name) {
        this.name = name;
    }
}
