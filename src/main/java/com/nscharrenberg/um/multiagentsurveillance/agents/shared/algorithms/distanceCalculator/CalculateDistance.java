package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public interface CalculateDistance {

    /**
     * Calculate a distance between two points
     * @param tileX Position X
     * @param tileY Position Y
     * @return the distance between two points
     */
    double compute(Tile tileX, Tile tileY);
}
