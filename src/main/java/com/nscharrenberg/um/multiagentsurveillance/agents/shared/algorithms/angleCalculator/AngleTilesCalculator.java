package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Angle.Angle;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class AngleTilesCalculator {

    /**
     * Calculate the game angle from X to Y
     * @param tileX Position X
     * @param tileY Position Y
     * @return the game angle, which demonstrates the direction from X to Y
     */
    public static Angle computeAngle(Tile tileX, Tile tileY) {
        int x = tileY.getX() - tileX.getX();
        int y = tileY.getY() - tileX.getY();

        int absX = Math.abs(x);
        int absY = Math.abs(y);

        if(x == 0 && y == 0)
            System.out.println("Game angle is calculated incorrectly, because tiles are the same");


        if(x == 0){

            if(y > 0) return Angle.DOWN;
            else return Angle.UP;

        } else if(y == 0){

            if(x > 0) return Angle.RIGHT;
            else return Angle.LEFT;

        } else if(x > 0){

            if(y > 0) {
                if (absX > absY) return Angle.RIGHT;
                else return Angle.DOWN;
            } else {
                if(absX > absY) return Angle.RIGHT;
                else return Angle.UP;
            }

        } else {

            if(y > 0) {
                if (absX > absY) return Angle.LEFT;
                else return Angle.DOWN;
            } else {
                if(absX > absY) return Angle.LEFT;
                else return Angle.UP;
            }

        }
    }
}
