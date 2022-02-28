package com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator;

import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.Frontier;
import com.nscharrenberg.um.multiagentsurveillance.agents.frontier.yamauchi.comparator.IWeightComparator;

public class SimpleWeightComparator implements IWeightComparator {
    @Override
    public Frontier compare(Frontier frontier, Frontier bestFrontier) {
        if (frontier.getQueueNode() == null && bestFrontier.getQueueNode() == null) {
            return frontier.getUnknownAreas() < bestFrontier.getUnknownAreas() ? frontier:bestFrontier;
        }

        double x =  frontier.getUnknownAreas() * frontier.getQueueNode().getDistance();
        double y =  bestFrontier.getUnknownAreas() * bestFrontier.getQueueNode().getDistance();

        return x < y ? frontier:bestFrontier;
    }
}
