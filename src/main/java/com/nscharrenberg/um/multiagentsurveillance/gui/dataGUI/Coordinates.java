package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

public class Coordinates {

    public float x;
    public float y;

    public Coordinates(Object x, Object y) {
        this.x = Float.parseFloat(x.toString());
        this.y = Float.parseFloat(y.toString());
    }
}
