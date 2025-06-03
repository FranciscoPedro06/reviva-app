package com.reviva.app.models;

import java.util.UUID;
public class User {
    private String uId;
    private String name;
    private String email;
    private String password;

    // Constructor
    public User(String uId, String name, String email, String password) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getuId() {
        return uId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public String setName(String name) {
        return name;
    }

    public String setEmail(String email) {
        return email;
    }

    public String setPassword(String password) {
        return password;
    }
}

