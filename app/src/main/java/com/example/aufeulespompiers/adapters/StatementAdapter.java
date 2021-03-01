package com.example.aufeulespompiers.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.model.SensorStatement;

import java.util.List;

public class StatementAdapter extends ArrayAdapter<SensorStatement> {

    Context context;

    public StatementAdapter(@NonNull Context context, @NonNull List<SensorStatement> objects) {
        super(context, R.layout.sensor_statement_item, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SensorStatement statement = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.sensor_statement_item, parent, false);
        }

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
    public SensorStatement getItem(int position) {
        return super.getItem(position);
    }

}
