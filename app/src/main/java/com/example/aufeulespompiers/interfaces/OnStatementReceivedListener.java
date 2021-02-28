package com.example.aufeulespompiers.interfaces;

import com.example.aufeulespompiers.model.Statement;

import java.util.ArrayList;

public interface OnStatementReceivedListener {
    void onStatementListReceived(ArrayList<Statement> result);
}
