package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class ManhattanDistance {

    public static double compute(Tile tileX, Tile tileY){
        double x = Math.abs(tileX.getX() - tileY.getX());
        double y = Math.abs(tileX.getY() - tileY.getY());
        return x + y;
    }
}
