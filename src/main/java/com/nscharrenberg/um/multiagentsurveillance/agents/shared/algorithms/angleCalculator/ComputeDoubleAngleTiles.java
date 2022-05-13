package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.angleCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class ComputeDoubleAngleTiles {

    public static double computeAngle(Tile tileX, Tile tileY){
        int x = tileY.getX() - tileX.getX();
        int y = tileY.getY() - tileX.getY();
        return Math.atan2(y, x);
    }
}
