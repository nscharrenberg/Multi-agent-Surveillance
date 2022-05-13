package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.intruder;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard.IWeightComparatorGuard;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.guard.MinDistanceUnknownAreaComparator;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.CalculateDistance;
import com.nscharrenberg.um.multiagentsurveillance.agents.shared.algorithms.distanceCalculator.ManhattanDistance;
import com.nscharrenberg.um.multiagentsurveillance.headless.models.Map.Tile;

/**
 * Compare frontiers by the distance to the target
 * If distances are equal, we use a minimal distance or a number of unknown tiles comparator
 */
public class CloseToTarget implements IWeightComparatorIntruder {

    CalculateDistance calculateDistance = new ManhattanDistance();
    IWeightComparatorGuard calculateBestFrontier = new MinDistanceUnknownAreaComparator();

    @Override
    public Frontier compare(Frontier frontier, Frontier bestFrontier, Tile target) {

        if(target != null) {
            double frontierDistance = calculateDistance.compute(frontier.getTarget(), target);
            double bestFrontierDistance = calculateDistance.compute(bestFrontier.getTarget(), target);

            if (frontierDistance < bestFrontierDistance) {
                return frontier;
            } else if (frontierDistance == bestFrontierDistance) {
                return calculateBestFrontier.compare(frontier, bestFrontier);
            }

        } else {
            return calculateBestFrontier.compare(frontier, bestFrontier);
        }

        return bestFrontier;

    }

}
