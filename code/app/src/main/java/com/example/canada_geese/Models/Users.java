package com.example.canada_geese.Models;

public class Users {
    private String username;
    private String image_profile;
    private String UserID;
    private String about;

    // For Firebase empty constructor
    public Users() {
    }

    public Users(String username, String image_profile) {
        this.username = username;
        this.image_profile = image_profile;
        this.about = about;
        this.UserID = UserID;
    }

    public String getUsername() {
        return username;
    }
    public String getImage_profile() {
        return image_profile;
    }
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUserId( ) {
        return UserID;
    }

    public void setUserId(String UserId) {
        this.UserID = UserId;
    }
}


