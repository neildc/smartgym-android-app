package com.example.doge.smartgym3;

/**
 * Created by neil on 17/11/16.
 */

public class BragNotification extends Notification {
    public int weight;
    public String exercise;
    public int exercise_id;
    public String from_name;

    public BragNotification(
            String type,
            int remote_id,
            String exercise,
            int exercise_id,
            int weight,
            String from_name
            )
    {

        super(type, remote_id);

        this.exercise = exercise;
        this.exercise_id = exercise_id;
        this.weight = weight;
        this.from_name = from_name;
    }


}
