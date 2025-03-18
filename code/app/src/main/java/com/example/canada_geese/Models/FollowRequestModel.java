package com.example.canada_geese.Models;


public class FollowRequestModel {
    private String requestID;
    private String requestUsername;
    private String status;

    public FollowRequestModel() {
        // For Firestore
    }
    public FollowRequestModel(String requestID, String requestUsername, String status) {
        this.requestID = requestID;
        this.requestUsername = requestUsername;
        this.status = status;
    }

    public String getRequestID() {
       return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestUsername() {
        return requestUsername;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

