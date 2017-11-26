package me.zeejfps.paw.models;

public class Account {

    private String authToken;

    public Account(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

}
