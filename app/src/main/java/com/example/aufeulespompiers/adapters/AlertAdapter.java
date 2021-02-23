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
import com.example.aufeulespompiers.activities.AlertActivity;
import com.example.aufeulespompiers.model.Alert;

import java.util.List;

public class AlertAdapter  extends ArrayAdapter<Alert> {

    Context context;

    public AlertAdapter(@NonNull Context context, @NonNull List<Alert> objects) {
        super(context, R.layout.alert_item, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Alert alert = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alert_item, parent, false);
        }

        convertView.setOnClickListener(view -> {
            Intent intent = new Intent(context, AlertActivity.class);
            context.startActivity(intent);
        });

        return convertView;
    }

    @Nullable
    @Override
    public Alert getItem(int position) {
        return super.getItem(position);
    }
}
