package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;

public class RightAngle {

    /**
     * Get right side game angle (For example: Angle.Left -> Angle.Up)
     * @param angle Game angle
     * @return the right side angle
     */
    public static Action getRightAngle(Action angle){

        if(!(angle.equals(Action.UP) || angle.equals(Action.DOWN) || angle.equals(Action.LEFT) || angle.equals(Action.RIGHT)))
            throw new RuntimeException("Wrong Angle");

        if(angle.equals(Action.UP)){
            return Action.RIGHT;
        } else if(angle.equals(Action.LEFT)){
            return Action.UP;
        } else if(angle.equals(Action.RIGHT)){
            return Action.DOWN;
        } else{
            return Action.LEFT;
        }
    }

}
