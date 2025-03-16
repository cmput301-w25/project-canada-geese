package com.example.canada_geese.Models;

public class FollowRequest {
    private String username;
    private String requestID;

    public FollowRequest(String username, String requestID) {
        this.username = username;
        this.requestID = requestID;
    }

    public String getUsername() {
        return username;
    }

    public String getRequestID() {
        return requestID;
    }
}
