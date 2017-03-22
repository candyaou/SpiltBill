package com.paspanaassarasee.spiltbill;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CardAdapter extends ArrayAdapter<Card> {
    public CardAdapter(Context context, int resource, List<Card> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        CircleImageView photoImageView = (CircleImageView) convertView.findViewById(R.id.photoImageView);
        TextView tvTitle = (TextView) convertView.findViewById(R.id.title);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.username);
        TextView tvTime = (TextView) convertView.findViewById(R.id.time);

        Card message = getItem(position);
        Log.wtf("URL",message.getPhotoUrl());

        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            tvTitle.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.VISIBLE);
            Glide.with(photoImageView.getContext())
                    .load(message.getPhotoUrl())
//                    .bitmapTransform(new CropCircleTransformation(photoImageView.getContext()))
                    .override(150,150)
                    .into(photoImageView);
            tvTitle.setText(message.getTitle());
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            photoImageView.setVisibility(View.VISIBLE);
            tvTitle.setText(message.getTitle());
        }

        tvUsername.setText(message.getName());
        tvTime.setText(message.getTime());

        return convertView;
    }

    public void updateList(){
        notifyDataSetChanged();
    }
}
