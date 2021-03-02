package com.example.aufeulespompiers.Services;

import android.app.Activity;
import android.content.SharedPreferences;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Singleton class to be able to use the same variable in all the application
public class AuthenticationService {

    long currentUser;
    ArrayList<Long> userAutrorized;
    private static final AuthenticationService authenticationService = new AuthenticationService();

    public static AuthenticationService getInstance(){
        return authenticationService;
    }

    private AuthenticationService(){

        this.currentUser = 0;
        userAutrorized = new ArrayList<>();
        userAutrorized.add((long) 41975300);
    }

    public long getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(long currentUser) {
        this.currentUser = currentUser;
    }

    // Long and not long for use .contains method for check the authorized user
    public ArrayList<Long> getUserAutrorized() {
        return userAutrorized;
    }

    public void setUserAutrorized(ArrayList<Long> userAutrorized) {
        this.userAutrorized = userAutrorized;
    }
}
