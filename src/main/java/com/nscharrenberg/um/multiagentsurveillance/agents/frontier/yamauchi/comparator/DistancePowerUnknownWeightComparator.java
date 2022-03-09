package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.IWeightComparator;

public class DistancePowerUnknownWeightComparator implements IWeightComparator {
    @Override
    public Frontier compare(Frontier frontier, Frontier bestFrontier) {
        if (frontier.getQueueNode() == null && bestFrontier.getQueueNode() == null) {
            return frontier.getUnknownAreas() < bestFrontier.getUnknownAreas() ? frontier:bestFrontier;
        }
        
        double x = Math.pow(frontier.getQueueNode().getDistance(), frontier.getUnknownAreas());
        double y = Math.pow(bestFrontier.getQueueNode().getDistance(), bestFrontier.getUnknownAreas());
        
        return x < y ? frontier:bestFrontier;
    }
}
