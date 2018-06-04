package com.example.android.movieapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hassa on 2/20/2018.
 */

public class TrailerAdapter extends ArrayAdapter<TrailerModel> {
    public TrailerAdapter(@NonNull Context context, @NonNull ArrayList<TrailerModel> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        TrailerModel trailerModel = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_list_item, parent, false);
        }

        TextView keyTextView = convertView.findViewById(R.id.trailer_text_view);
        keyTextView.setText("Trailer " + (position+1));

        return convertView;


    }
}
