package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

public class OppositeAngle {

    /**
     * Opposite angle
     * @param angle Game angle
     * @return the opposite game angle
     */
    public static Action getOppositeAngle(Action angle){

        if(!(angle.equals(Action.UP) || angle.equals(Action.DOWN) || angle.equals(Action.LEFT) || angle.equals(Action.RIGHT)))
            throw new RuntimeException("Wrong Angle");

        if(angle.equals(Action.UP)){
            return Action.DOWN;
        } else if(angle.equals(Action.LEFT)){
            return Action.RIGHT;
        } else if(angle.equals(Action.RIGHT)){
            return Action.LEFT;
        } else {
            return Action.UP;
        }
    }
}
