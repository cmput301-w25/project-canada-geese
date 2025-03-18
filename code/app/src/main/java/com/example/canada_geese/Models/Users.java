package com.example.canada_geese.Models;

public class Users {
    private String username;
    private String image_profile;

    // For Firebase empty constructor
    public Users() {
    }

    public Users(String username, String image_profile) {
        this.username = username;
        this.image_profile = image_profile;
    }

    public String getUsername() {
        return username;
    }
    public String getImage_profile() {
        return image_profile;
    }

}


