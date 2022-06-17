package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

public class LeftAngle {

    /**
     * Get right side game angle (For example: Angle.Left -> Angle.Down)
     * @param angle Game angle
     * @return the left side angle
     */
    public static Action getLeftAngle(Action angle){

        if(!(angle.equals(Action.UP) || angle.equals(Action.DOWN) || angle.equals(Action.LEFT) || angle.equals(Action.RIGHT)))
            throw new RuntimeException("Wrong Angle");

        if(angle.equals(Action.UP)){
            return Action.LEFT;
        } else if(angle.equals(Action.LEFT)){
            return Action.DOWN;
        } else if(angle.equals(Action.RIGHT)){
            return Action.UP;
        } else{
            return Action.RIGHT;
        }
    }

}
