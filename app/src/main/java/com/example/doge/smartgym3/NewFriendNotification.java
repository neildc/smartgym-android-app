package com.example.doge.smartgym3;

/**
 * Created by neil on 17/11/16.
 */

public class NewFriendNotification extends Notification {
    int facebook_uid;
    int user_id;
    String name;


    public NewFriendNotification(
            String type,
            int remote_id,
            int facebook_uid,
            int user_id,
            String name
            )
    {

        super(type, remote_id);
        this.facebook_uid = facebook_uid;
        this.user_id = user_id;
        this.name = name;

    }


}
