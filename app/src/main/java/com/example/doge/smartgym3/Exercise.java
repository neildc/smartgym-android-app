package com.example.doge.smartgym3;

/**
 * Created by DOGE on 6/11/2016.
 */

public class Exercise {
    public String exerciseName;
    public int sets;
    public int reps;
    public int rest;

    public Exercise(String name, int sets, int reps, int rest) {
        this.exerciseName = name;
        this.sets = sets;
        this.reps = reps;
        this.rest = rest;
    }
}
