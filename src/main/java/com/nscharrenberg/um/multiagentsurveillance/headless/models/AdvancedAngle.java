package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public enum AdvancedAngle {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    TOP_LEFT(-1, -1),
    TOP_RIGHT(1, -1),
    BOTTOM_LEFT(-1, 1),
    BOTTOM_RIGHT(1, 1);

    AdvancedAngle(int xIncrement, int yIncrement) {
        this.xIncrement = xIncrement;
        this.yIncrement = yIncrement;
    }

    private int xIncrement = 0;
    private int yIncrement = 0;

    public int getxIncrement() {
        return xIncrement;
    }

    public int getyIncrement() {
        return yIncrement;
    }
}
