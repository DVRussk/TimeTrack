package com.example.timetrack;

import android.widget.ToggleButton;

import java.util.ArrayList;

public class TimeList {
    public ToggleButton btn;
    public int btnNum;
    protected String name;
    public int number;
    public ArrayList<Long> listOfExitEnterPoints;
    public long spentTime;

    public TimeList(ArrayList<Long> listOfExitEnterPoints, long spentTime, ToggleButton btn, int btnNum, int number, String name) {
        this.btn = btn;
        this.listOfExitEnterPoints = listOfExitEnterPoints;
        this.spentTime = spentTime;
        this.number = number;
        this.name = name;
        this.btnNum = btnNum;
    }
    public int getNumber(){
        return this.number;
    }
    public int getBtnNum(){
        return this.btnNum;
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
        if(this.listOfExitEnterPoints.size() == 0) return -1;
        return this.listOfExitEnterPoints.get(this.listOfExitEnterPoints.size()-1);
    }
    public String getName(){
        return this.name;
    }
}
