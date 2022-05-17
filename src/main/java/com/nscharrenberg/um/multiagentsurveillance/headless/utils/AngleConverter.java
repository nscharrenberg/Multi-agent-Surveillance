package com.nscharrenberg.um.multiagentsurveillance.headless.utils;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.AdvancedAngle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;

import java.util.ArrayList;
import java.util.List;

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

    public static ArrayList<Angle> split(AdvancedAngle inputangle) {
        ArrayList<Angle> angleset = new ArrayList<Angle>();

        if(inputangle == AdvancedAngle.UP)
            angleset.add(Angle.UP);

        if(inputangle == AdvancedAngle.DOWN)
            angleset.add(Angle.DOWN);

        if(inputangle == AdvancedAngle.LEFT)
            angleset.add(Angle.LEFT);

        if(inputangle == AdvancedAngle.RIGHT)
            angleset.add(Angle.RIGHT);

        if(inputangle == AdvancedAngle.TOP_RIGHT) {
            angleset.add(Angle.UP);
            angleset.add(Angle.RIGHT);
        }

        if(inputangle == AdvancedAngle.TOP_LEFT) {
            angleset.add(Angle.UP);
            angleset.add(Angle.LEFT);
        }

        if(inputangle == AdvancedAngle.BOTTOM_RIGHT) {
            angleset.add(Angle.DOWN);
            angleset.add(Angle.LEFT);
        }

        if(inputangle == AdvancedAngle.BOTTOM_LEFT) {
            angleset.add(Angle.DOWN);
            angleset.add(Angle.LEFT);
        }

        return angleset;

    }

}
