package com.example.aufeulespompiers.interfaces;

import com.example.aufeulespompiers.model.Statement;

import java.util.ArrayList;

// Use for manage the Asynchronous return of firestore request
public interface OnAlertReceivedListener {
    void onAlertListReceived(ArrayList<Statement> result);
}
