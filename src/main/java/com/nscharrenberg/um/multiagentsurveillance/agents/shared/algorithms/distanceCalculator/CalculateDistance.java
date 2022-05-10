package com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator;

import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

public interface CalculateDistance {

    double compute(Tile tileX, Tile tileY);
}
