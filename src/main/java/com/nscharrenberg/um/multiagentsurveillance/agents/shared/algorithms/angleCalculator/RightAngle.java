package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;

public class RightAngle {

    /**
     * Get right side game angle (For example: Angle.Left -> Angle.Up)
     * @param angle Game angle
     * @return the right side angle
     */
    public static Angle getRightAngle(Angle angle){

        if(!(angle.equals(Angle.UP) || angle.equals(Angle.DOWN) || angle.equals(Angle.LEFT) || angle.equals(Angle.RIGHT)))
            throw new RuntimeException("Wrong Angle");

        if(angle.equals(Angle.UP)){
            return Angle.RIGHT;
        } else if(angle.equals(Angle.LEFT)){
            return Angle.UP;
        } else if(angle.equals(Angle.RIGHT)){
            return Angle.DOWN;
        } else{
            return Angle.LEFT;
        }
    }

}
