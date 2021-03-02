package com.example.aufeulespompiers.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.model.SensorStatement;
import com.example.aufeulespompiers.model.Statement;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class StatementAdapter extends ArrayAdapter<Statement> {

    Context context;

    public StatementAdapter(@NonNull Context context, @NonNull List<Statement> objects) {
        super(context, R.layout.sensor_statement_item, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Statement statement = getItem(position);
        TextView title = convertView.findViewById(R.id.titled);
        TextView date = convertView.findViewById(R.id.date);
        TextView temp = convertView.findViewById(R.id.temperature);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.sensor_statement_item, parent, false);
        }

        // if not logged
        if (statement.getAssignedTo() == 0) {
            title.setVisibility(View.GONE);
        } else {
            title.setText(String.valueOf(statement.getAssignedTo()));
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - hh:mm", Locale.getDefault());
        date.setText(simpleDateFormat.format(statement.getDate().toDate()));
        temp.setText(statement.getTemp() + "°C");

        /*
        convertView.setOnClickListener(view -> {
            Intent intent = new Intent(context, StatementActivity.class);
            context.startActivity(intent);
        });
         */

        return convertView;
    }

    @Nullable
    @Override
    public Statement getItem(int position) {
        return super.getItem(position);
    }

}
