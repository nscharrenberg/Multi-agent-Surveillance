package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public class Marker {
    private int type;
    private int range;
    private int duration;


    public Marker(int type, int range, int duration) {
        this.type = type;
        this.range = range;
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
