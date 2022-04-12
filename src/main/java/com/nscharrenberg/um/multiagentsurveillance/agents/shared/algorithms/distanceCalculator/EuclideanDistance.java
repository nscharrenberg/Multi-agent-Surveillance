package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public class EuclideanDistance {

    public static double compute(Tile tileX, Tile tileY){
        double x = Math.pow((tileX.getX() - tileY.getX()), 2);
        double y = Math.pow((tileX.getY() - tileY.getY()), 2);
        return Math.sqrt(x+y);
    }

}
