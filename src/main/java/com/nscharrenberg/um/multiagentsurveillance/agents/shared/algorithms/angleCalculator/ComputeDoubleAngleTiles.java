package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class ComputeDoubleAngleTiles {

    /**
     * Calculate the real angle between two points
     * @param tileX Position X
     * @param tileY Position Y
     * @return the real angle between X and Y
     */
    public static double computeAngle(Tile tileX, Tile tileY){
        int x = tileY.getX() - tileX.getX();
        int y = tileY.getY() - tileX.getY();
        return Math.atan2(y, x);
    }
}
