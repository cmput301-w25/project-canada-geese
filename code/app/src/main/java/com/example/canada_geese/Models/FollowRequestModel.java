package com.example.canada_geese.Models;


public class FollowRequestModel {
    private String username;
    private String status;

    public FollowRequestModel() {
        // For Firestore
    }
    public FollowRequestModel(String username, String status) {
        this.username = username;
        this.status = status;
    }


    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

