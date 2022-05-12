package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;


/**
 * Diamond range
 */
public class ManhattanDistance implements CalculateDistance{

    @Override
    public double compute(Tile tileX, Tile tileY){
        double x = Math.abs(tileX.getX() - tileY.getX());
        double y = Math.abs(tileX.getY() - tileY.getY());
        return x + y;
    }
}
