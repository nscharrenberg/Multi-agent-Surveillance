package com.nscharrenberg.um.multiagentsurveillance.headless.utils.recorder.json;

public class Coordinates {


    private float x;
    private float y;

    public Coordinates(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float x() {
        return x;
    }


    public float y() {
        return y;
    }
}
