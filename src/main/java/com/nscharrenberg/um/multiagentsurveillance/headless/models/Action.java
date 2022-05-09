package com.nscharrenberg.um.multiagentsurveillance.headless.models;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;

public enum Action {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0),
    PLACE_MARKER_DEADEND(0, 0),
    PLACE_MARKER_TARGET(0, 0),
    PLACE_MARKER_GUARDSPOTTED(0, 0),
    PLACE_MARKER_INTRUDERSPOTTED(0, 0),
    PLACE_MARKER_TELEPORTER(0, 0),
    PLACE_MARKER_SHADED(0, 0);


    Action(int xIncrement, int yIncrement) {
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

    public static AdvancedAngle toAdvancedAngle(Action action) {
        if (action.equals(Action.DOWN)) return AdvancedAngle.DOWN;
        if (action.equals(Action.LEFT)) return AdvancedAngle.LEFT;
        if (action.equals(Action.RIGHT)) return AdvancedAngle.RIGHT;

        return AdvancedAngle.UP;
    }
}
