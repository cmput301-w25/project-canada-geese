package com.example.canada_geese.Models;

/**
 * Represents a follow request sent by a user.
 */
public class FollowRequestModel {
    private String username;
    private String status;

    /**
     * No-argument constructor required for Firestore deserialization.
     */
    public FollowRequestModel() {}

    /**
     * Constructs a FollowRequestModel with specified username and status.
     *
     * @param username the username of the user sending the request
     * @param status   the status of the request (e.g., "pending", "accepted")
     */
    public FollowRequestModel(String username, String status) {
        this.username = username;
        this.status = status;
    }

    /**
     * Returns the username of the requesting user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the current status of the follow request.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the follow request.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
