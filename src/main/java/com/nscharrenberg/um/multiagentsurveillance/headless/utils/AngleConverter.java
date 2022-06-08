package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

public class AngleConverter {

    /**
     * Converting an angle to a predefined Action field.
     * @param angle - angle in degrees to be converted
     * @return - predefined Action {UP, DOWN, LEFT, RIGHT}
     */
    public static Action convert(double angle) {
        // Right when Action is between 45 and 135 degrees
        if (angle > 45 && angle <= 135) {
            return Action.RIGHT;
        }

        // Down when Action is between 135 and 225 degrees
        if (angle > 135 && angle <= 225) {
            return Action.DOWN;
        }

        // Left when Action is between 225 and 315 degrees
        if (angle > 225 && angle <= 315) {
            return Action.LEFT;
        }

        // UP when Action is between -45 (315) and 45 degrees
        return Action.UP;
    }
}
