package com.androidapptraining;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public
class MyAdapter extends ArrayAdapter {

    String[] names;
    int[] images;

    public MyAdapter(@NonNull Context context, String[] names, int[] images) {
        super(context, R.layout.custom_list_view, R.id.userName, names);
        this.names = names;
        this.images = images;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       /* LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_list_view, parent, false);
        ImageView userProfile = row.findViewById(R.id.userProfile);
        TextView userName = row.findViewById(R.id.userName);
        userProfile.setImageResource(images[position]);
        userName.setText(names[position]);*/
        return null;
    }
}