package com.example.timetrack;

import android.widget.ToggleButton;

import java.util.ArrayList;

public class TimeList {
    public ToggleButton btn;
    private ArrayList<Long> listOfExitEnterPoints;
    public long spentTime;

    public TimeList(ArrayList<Long> listOfExitEnterPoints, long spentTime, ToggleButton btn) {
        this.btn = btn;
        this.listOfExitEnterPoints = listOfExitEnterPoints;
        this.spentTime = spentTime;
    }

    public ToggleButton getButton() {
        return this.btn;
    }

    public long getSpentTime() {
        return this.spentTime;
    }

    public ArrayList<Long> getListOfExitEnterPoints() {
        return listOfExitEnterPoints;
    }
    public void addEnterPoint(long point){
        this.listOfExitEnterPoints.add(point);
    }
    public void addExitPoint(long point){
        this.spentTime += point - this.getLastPoint();
        this.listOfExitEnterPoints.add(point);
    }
    public long getLastPoint(){
        return this.listOfExitEnterPoints.get(this.listOfExitEnterPoints.size()-1);
    }
}
