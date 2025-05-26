package com.reviva.app.models;

import java.util.UUID;

public class User {
    private String uId;
    private String name;
    private String email;
    private String password;

    public User(String uId, String name, String email, String password) {
        this.uId = uId;
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
