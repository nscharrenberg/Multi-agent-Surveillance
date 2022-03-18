package com.nscharrenberg.um.multiagentsurveillance.gui.dataGUI;

public class Coordinates {

    protected float x;
    protected float y;

    public Coordinates(Object x, Object y) {
        this.x = Float.parseFloat(x.toString());
        this.y = Float.parseFloat(y.toString());
    }
}
