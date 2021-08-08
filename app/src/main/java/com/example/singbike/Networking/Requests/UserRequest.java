package com.example.singbike.Networking.Requests;

import com.example.singbike.Models.User;

/**
 * Network Requests associated to User
 * This Request Class will be used for Sign In and Sign Up Requests
 * */

public class UserRequest {

    private String username, email, password;

    public UserRequest (String username, String email) {
        this.username = username;
        this.email = email;
    }

    public UserRequest (String username, String email, String password) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public UserRequest (User user) {
        this.email = user.getEmail();
        this.username = user.getUsername();
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
