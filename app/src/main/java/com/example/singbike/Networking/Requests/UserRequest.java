package com.example.singbike.NetworkRequests;

/**
 * Network Requests associated to User
 * This Request Class will be used for Sign In and Sign Up Requests
 * */

public class UserRequest {

    private String username, email, password;

    public UserRequest (String username, String email, String password) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public String getUsername () {
        return this.username;
    }

    public String getEmail () { return this.email; }

    public String getPassword () { return this.password; }

    public void setUsername (String username) {
        this.username = username;
    }

    public void setEmail (String email) {
        this.email = email;
    }

    public void setPassword (String password) {
        this.password = password;
    }
}
