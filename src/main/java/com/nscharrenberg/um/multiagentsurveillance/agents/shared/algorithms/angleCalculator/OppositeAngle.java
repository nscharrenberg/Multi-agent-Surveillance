package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;

public class OppositeAngle {

    /**
     * Opposite angle
     * @param angle Game angle
     * @return the opposite game angle
     */
    public static Angle getOppositeAngle(Angle angle){

        if(!(angle.equals(Angle.UP) || angle.equals(Angle.DOWN) || angle.equals(Angle.LEFT) || angle.equals(Angle.RIGHT)))
            throw new RuntimeException("Wrong Angle");

        if(angle.equals(Angle.UP)){
            return Angle.DOWN;
        } else if(angle.equals(Angle.LEFT)){
            return Angle.RIGHT;
        } else if(angle.equals(Angle.RIGHT)){
            return Angle.LEFT;
        } else {
            return Angle.UP;
        }
    }
}
