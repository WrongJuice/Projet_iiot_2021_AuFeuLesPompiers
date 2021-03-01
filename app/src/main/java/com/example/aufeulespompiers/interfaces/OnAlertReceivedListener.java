package com.example.aufeulespompiers.interfaces;

import com.example.aufeulespompiers.model.Statement;

import java.util.ArrayList;

public interface OnAlertReceivedListener {
    void onAlertListReceived(ArrayList<Statement> result);
}
