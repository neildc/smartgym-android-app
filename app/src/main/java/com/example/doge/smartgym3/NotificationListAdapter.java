package com.example.doge.smartgym3;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DOGE on 13/11/16.
 */

public class NotificationListAdapter extends ArrayAdapter<Notification> {
    public NotificationListAdapter(Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Notification notification = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_notification, parent, false);
        }
        // Lookup view for data population
        TextView messageTextView= (TextView) convertView.findViewById(R.id.message);
        TextView weighTextView = (TextView) convertView.findViewById(R.id.weight);
        CircleImageView profilePic = (CircleImageView) convertView.findViewById(R.id.friendProfilePic);

        // Populate the data into the template view using the data object
        if (notification.type.equals("brag")){
            brag(notification, messageTextView, weighTextView, profilePic);

        } else if (notification.type.equals("friend_join")) {
            friend_join(notification, messageTextView, weighTextView, profilePic);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private void brag(Notification notification, TextView message, TextView weight, CircleImageView profilePic) {
        String s = notification.who_from_name + " does heavier " + notification.exercise + "!";
        SpannableString notificationText = new SpannableString(s);
        notificationText.setSpan(new UnderlineSpan(), s.length() - 1 - notification.exercise.length(), s.length(), 0);
        message.setText(notificationText);

        s = String.valueOf(notification.weight) + "\nkg";
        SpannableString weightText = new SpannableString(s);
        weightText.setSpan(new RelativeSizeSpan(0.3f), s.length() - 2, s.length(), 0); // set size
        weight.setText(weightText);
        weight.setVisibility(View.VISIBLE);
        profilePic.setVisibility(View.GONE);
    }

    private void friend_join(Notification notification, TextView message, TextView weight, CircleImageView profilePic) {
        message.setText(notification.who_from + " has joined !");
        profilePic.setImageBitmap(notification.who_from_profile_pic);
        weight.setVisibility(View.GONE);
        profilePic.setVisibility(View.VISIBLE);
    }


}
