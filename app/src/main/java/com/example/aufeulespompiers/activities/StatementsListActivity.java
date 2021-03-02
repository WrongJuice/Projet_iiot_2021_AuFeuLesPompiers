package com.example.aufeulespompiers.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.Services.FirestoreService;
import com.example.aufeulespompiers.adapters.StatementAdapter;
import com.example.aufeulespompiers.model.Statement;

import java.util.ArrayList;

public class StatementsListActivity extends AppCompatActivity {

    ListView statementsList;
    TextView noStatementText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statements_list);

        FirestoreService firestoreService = new FirestoreService();
        ArrayList<Statement> statements = new ArrayList<>();
        firestoreService.getStatements(result -> {
            for (Statement statement : result)
                if (statement.getBeacon().equals(getIntent().getStringExtra("statementId")))
                    statements.add(statement);
        });

        statementsList = findViewById(R.id.statements_list);
        noStatementText = findViewById(R.id.no_statements);

        if (statements.isEmpty()) {
            statementsList.setVisibility(View.GONE);
            noStatementText.setVisibility(View.VISIBLE);
        } else {
            StatementAdapter statementAdapter = new StatementAdapter(this, statements);
            statementsList.setAdapter(statementAdapter);
        }

    }
}
