package com.example.canada_geese.Models;

/**
 * Represents a user in the Canada Geese app.
 * Contains user profile information such as username, profile image, user ID, and about/bio.
 */
public class Users {
    private String username;
    private String image_profile;
    private String UserID;
    private String about;

    /**
     * Default constructor required for Firebase.
     */
    public Users() {}

    /**
     * Constructs a Users object with a username and profile image.
     *
     * @param username       the user's display name
     * @param image_profile  URL or path to the user's profile image
     */
    public Users(String username, String image_profile) {
        this.username = username;
        this.image_profile = image_profile;
    }

    /**
     * Returns the username.
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the profile image URL.
     *
     * @return the profile image URL
     */
    public String getImage_profile() {
        return image_profile;
    }

    /**
     * Returns the user's bio or about section.
     *
     * @return the about text
     */
    public String getAbout() {
        return about;
    }

    /**
     * Sets the user's about/bio section.
     *
     * @param about the bio text
     */
    public void setAbout(String about) {
        this.about = about;
    }

    /**
     * Returns the user's Firebase ID.
     *
     * @return the user ID
     */
    public String getUserId() {
        return UserID;
    }

    /**
     * Sets the user's Firebase ID.
     *
     * @param UserId the user ID
     */
    public void setUserId(String UserId) {
        this.UserID = UserId;
    }
}
