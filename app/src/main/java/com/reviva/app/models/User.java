package com.reviva.app.models;

public class User {
    private String uId;
    private String name;
    private String email;
    private String password;

    // Constructors
    public User() {
    }

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
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
