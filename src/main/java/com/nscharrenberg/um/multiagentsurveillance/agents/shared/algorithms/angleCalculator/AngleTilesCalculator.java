package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Action;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class AngleTilesCalculator {

    /**
     * Calculate the game angle from X to Y
     * @param tileX Position X
     * @param tileY Position Y
     * @return the game angle, which demonstrates the direction from X to Y
     */
    public static Action computeAngle(Tile tileX, Tile tileY) {
        int x = tileY.getX() - tileX.getX();
        int y = tileY.getY() - tileX.getY();

        int absX = Math.abs(x);
        int absY = Math.abs(y);

        if(x == 0 && y == 0)
            System.out.println("Game angle is calculated incorrectly, because tiles are the same");


        if(x == 0){

            if(y > 0) return Action.DOWN;
            else return Action.UP;

        } else if(y == 0){

            if(x > 0) return Action.RIGHT;
            else return Action.LEFT;

        } else if(x > 0){

            if(y > 0) {
                if (absX > absY) return Action.RIGHT;
                else return Action.DOWN;
            } else {
                if(absX > absY) return Action.RIGHT;
                else return Action.UP;
            }

        } else {

            if(y > 0) {
                if (absX > absY) return Action.LEFT;
                else return Action.DOWN;
            } else {
                if(absX > absY) return Action.LEFT;
                else return Action.UP;
            }

        }
    }
}
