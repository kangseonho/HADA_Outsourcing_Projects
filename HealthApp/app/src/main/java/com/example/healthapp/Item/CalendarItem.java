package com.example.healthapp.Item;

import android.view.View;

public class CalendarItem {
    private int dayValue;
    private int arm = View.INVISIBLE;
    private int shoulder = View.INVISIBLE;
    private int leg = View.INVISIBLE;

    public CalendarItem(int dayValue){
        this.dayValue = dayValue;
    }

    public void setArm(int arm) {
        this.arm = arm;
    }

    public void setLeg(int leg) {
        this.leg = leg;
    }

    public void setShoulder(int shoulder) {
        this.shoulder = shoulder;
    }

    public int getDay(){
        return dayValue;
    }

    public int isArm() {
        return arm;
    }

    public int isShoulder() {
        return shoulder;
    }

    public int isLeg() {
        return leg;
    }
}
