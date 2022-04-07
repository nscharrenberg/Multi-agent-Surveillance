package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;

public class AngleConverter {

    /**
     * Converting an angle to a predefined Angle field.
     * @param angle - angle in degrees to be converted
     * @return - predefined Angle {UP, DOWN, LEFT, RIGHT}
     */
    public static Angle convert(double angle) {
        // Right when Angle is between 45 and 135 degrees
        if (angle > 45 && angle <= 135) {
            return Angle.RIGHT;
        }

        // Down when Angle is between 135 and 225 degrees
        if (angle > 135 && angle <= 225) {
            return Angle.DOWN;
        }

        // Left when Angle is between 225 and 315 degrees
        if (angle > 225 && angle <= 315) {
            return Angle.LEFT;
        }

        // UP when Angle is between -45 (315) and 45 degrees
        return Angle.UP;
    }
}
