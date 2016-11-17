package com.example.doge.smartgym3;

import java.util.Date;

/**
 * Created by DOGE on 6/11/2016.
 */

public class Exercise {
    public int id;
    public String exerciseName;
    public int sets;
    public int reps;
    public int rest;
    public int weight;
    public String date;

    public Exercise(String name, int sets, int reps, int rest, int weight, String date, int id) {
        this.id = id;
        this.exerciseName = name;
        this.sets = sets;
        this.reps = reps;
        this.rest = rest;
        this.weight = weight;
        this.date = date;
    }
}
