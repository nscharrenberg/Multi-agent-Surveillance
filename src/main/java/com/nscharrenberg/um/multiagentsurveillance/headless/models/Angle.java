package com.nscharrenberg.um.multiagentsurveillance.headless.models;

public enum Angle {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    Angle(int xIncrement, int yIncrement) {
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

    public static AdvancedAngle toAdvancedAngle(Angle angle) {
        if (angle.equals(Angle.DOWN)) return AdvancedAngle.DOWN;
        if (angle.equals(Angle.LEFT)) return AdvancedAngle.LEFT;
        if (angle.equals(Angle.RIGHT)) return AdvancedAngle.RIGHT;

        return AdvancedAngle.UP;
    }
}
