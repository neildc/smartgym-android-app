package com.example.doge.smartgym3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsActivity extends AppCompatActivity {

    private WorkoutApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        app = (WorkoutApplication) getApplication();
        this.setSupportActionBar(myToolbar);
        this.getSupportActionBar().setTitle("Notifications");
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayList<Notification> arrayOfRecentNotifications = new ArrayList<>();

        Notification newNotification;
        newNotification = new Notification(null, "brag", 300, "Bicep Curls", "Jason", null);
        arrayOfRecentNotifications.add(newNotification);
        newNotification = new Notification(null, "friend_join", 0, null, "Bill", app.getProfilePic());
        arrayOfRecentNotifications.add(newNotification);
        newNotification = new Notification(null, "friend_join", 0, null, "Henry", app.getProfilePic());
        arrayOfRecentNotifications.add(newNotification);

        NotificationListAdapter recentAdapter = new NotificationListAdapter(this, arrayOfRecentNotifications);
        ListView notificationListView = (ListView) findViewById(R.id.notification_list);
        notificationListView.setAdapter(recentAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            finish();
            return true;
        }
        // other menu select events may be present here

        return super.onOptionsItemSelected(item);
    }
}
