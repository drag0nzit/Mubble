package com.example.mubble.models;

public class User {

    private String id;
    private String email;
    private String username;
    private String avatarUrl;

    public User() {
    }

    public User(String id, String email, String username, String avatarUrl) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.avatarUrl = avatarUrl;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}