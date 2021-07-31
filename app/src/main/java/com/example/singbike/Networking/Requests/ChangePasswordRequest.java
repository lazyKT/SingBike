package com.example.singbike.Networking.Requests;

public class ChangePasswordRequest {

    private String old_password, new_password;

    public ChangePasswordRequest (String old_password, String new_password) {
        this.new_password = new_password;
        this.old_password = old_password;
    }

    public void setOld_password (String oldPassword) {
        this.old_password = oldPassword;
    }

    public void setNew_password (String newPassword) {
        this.new_password = newPassword;
    }

    public String getOld_password () {
        return this.old_password;
    }

    public String getNew_password () {
        return this.new_password;
    }

}
