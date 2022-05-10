package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator;


import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

/**
 * Square range
 */
public class ChessBoardDistance implements CalculateDistance{

    @Override
    public double compute(Tile tileX, Tile tileY) {
        double x = Math.abs(tileX.getX() - tileY.getX());
        double y = Math.abs(tileX.getY() - tileY.getY());
        return Math.max(x, y);
    }
}
