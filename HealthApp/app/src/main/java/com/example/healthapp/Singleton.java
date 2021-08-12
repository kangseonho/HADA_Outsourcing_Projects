package com.example.healthapp;

public class Singleton {

    public static Singleton instance = null;
    private int armExercise;
    private int legExercise;
    private int shoulderExercise;

    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    public void setArmExercise(int armExercise) {
        this.armExercise = armExercise;
    }

    public void setLegExercise(int legExercise) {
        this.legExercise = legExercise;
    }

    public void setShoulderExercise(int shoulderExercise) {
        this.shoulderExercise = shoulderExercise;
    }

    public int getArmExercise() {
        return armExercise;
    }

    public int getLegExercise() {
        return legExercise;
    }

    public int getShoulderExercise() {
        return shoulderExercise;
    }
}
