package com.example.aufeulespompiers.Services;

import android.app.Activity;
import android.content.SharedPreferences;

import java.time.LocalDateTime;

public class AuthenticationService {

    long currentUser;
    private static final AuthenticationService authenticationService = new AuthenticationService();

    public static AuthenticationService getInstance(){
        return authenticationService;
    }

    private AuthenticationService(){
        this.currentUser = 0;
    }

    public long getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(long currentUser) {
        this.currentUser = currentUser;
    }
}
