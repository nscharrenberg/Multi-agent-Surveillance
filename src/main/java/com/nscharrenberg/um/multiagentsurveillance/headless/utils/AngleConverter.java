package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

import java.util.ArrayList;
import java.util.List;

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

    public static ArrayList<Action> split(AdvancedAngle inputangle) {
        ArrayList<Action> angleset = new ArrayList<Action>();

        if(inputangle == AdvancedAngle.UP)
            angleset.add(Action.UP);

        if(inputangle == AdvancedAngle.DOWN)
            angleset.add(Action.DOWN);

        if(inputangle == AdvancedAngle.LEFT)
            angleset.add(Action.LEFT);

        if(inputangle == AdvancedAngle.RIGHT)
            angleset.add(Action.RIGHT);

        if(inputangle == AdvancedAngle.TOP_RIGHT) {
            angleset.add(Action.UP);
            angleset.add(Action.RIGHT);
        }

        if(inputangle == AdvancedAngle.TOP_LEFT) {
            angleset.add(Action.UP);
            angleset.add(Action.LEFT);
        }

        if(inputangle == AdvancedAngle.BOTTOM_RIGHT) {
            angleset.add(Action.DOWN);
            angleset.add(Action.LEFT);
        }

        if(inputangle == AdvancedAngle.BOTTOM_LEFT) {
            angleset.add(Action.DOWN);
            angleset.add(Action.LEFT);
        }

        return angleset;

    }

    public static AdvancedAngle AngleInverter(AdvancedAngle inputangle) {
        if(inputangle == AdvancedAngle.UP)
            return AdvancedAngle.DOWN;

        if(inputangle == AdvancedAngle.DOWN)
            return AdvancedAngle.UP;

        if(inputangle == AdvancedAngle.LEFT)
            return AdvancedAngle.RIGHT;

        if(inputangle == AdvancedAngle.RIGHT)
            return AdvancedAngle.LEFT;

        if(inputangle == AdvancedAngle.TOP_RIGHT)
            return AdvancedAngle.BOTTOM_LEFT;

        if(inputangle == AdvancedAngle.TOP_LEFT)
            return AdvancedAngle.BOTTOM_RIGHT;

        if(inputangle == AdvancedAngle.BOTTOM_RIGHT)
            return AdvancedAngle.TOP_LEFT;

        if(inputangle == AdvancedAngle.BOTTOM_LEFT)
            return AdvancedAngle.TOP_RIGHT;

        return null;
    }

}
