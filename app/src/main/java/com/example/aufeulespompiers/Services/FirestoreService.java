package com.example.aufeulespompiers.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.aufeulespompiers.interfaces.OnStatementReceivedListener;
import com.example.aufeulespompiers.model.Statement;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FirestoreService {

    private static final String TAG = "FirestoreService";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirestoreService() {
    }

    public void getStatements(OnStatementReceivedListener listener) {
        try{
            db.collection("alerts")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<Statement> statementList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Log.d(TAG, "onComplete: " + document.getData().get("temp"));
                                    Statement tempStatement = new Statement();
                                    tempStatement.setId(document.getId());
                                    tempStatement.setBeacon((String) document.getData().get("beacon"));
                                    tempStatement.setDate((Timestamp) document.getData().get("date"));
                                    tempStatement.setPosition((GeoPoint) document.getData().get("position"));
                                    tempStatement.setResolve((boolean) document.getData().get("resolve"));
                                    tempStatement.setTemp((long) document.getData().get("temp"));
                                    statementList.add(tempStatement);
                                }
                                listener.onStatementListReceived(statementList);

                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } finally {
            Log.d(TAG, "getStatements: finish");
        }
    }
}
